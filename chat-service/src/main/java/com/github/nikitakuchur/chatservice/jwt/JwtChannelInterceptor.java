package com.github.nikitakuchur.chatservice.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    private static final String HEADER_PREFIX = "Bearer ";

    private final JwtParser jwtParser;

    public JwtChannelInterceptor(@Value("${chat-service.jwt.secret-key}") String secretKey) {
        this.jwtParser = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build();
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (!StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }

        List<String> values = accessor.getNativeHeader("Authorization");

        if (values == null || values.isEmpty()) {
            throw new AccessDeniedException("JWT token not found.");
        }

        String authorization = values.get(0);

        if (!StringUtils.hasText(authorization) || !authorization.startsWith(HEADER_PREFIX)) {
            throw new AccessDeniedException("JWT token not found.");
        }

        String accessToken = authorization.substring(HEADER_PREFIX.length());
        Jws<Claims> claimsJws;
        try {
            claimsJws = jwtParser.parseSignedClaims(accessToken);
        } catch (JwtException e) {
            log.warn("The access token cannot be parsed: {}, reason={}", accessToken, e.getMessage());
            throw new AccessDeniedException(e.getMessage());
        }

        Claims body = claimsJws.getPayload();
        String username = body.getSubject();
        String role = body.get("role").toString();
        String sessionId = body.get("sessionId").toString();

        JwtUser jwtUser = new JwtUser(username, role, sessionId);

        List<GrantedAuthority> grantedAuthorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
        Authentication authentication = new UsernamePasswordAuthenticationToken(jwtUser, null, grantedAuthorities);
        accessor.setUser(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return message;
    }
}
