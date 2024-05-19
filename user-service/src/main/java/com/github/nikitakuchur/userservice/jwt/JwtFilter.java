package com.github.nikitakuchur.userservice.jwt;

import com.github.nikitakuchur.userservice.model.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * A request filter that decodes the given JWT token and puts its claims into the security context.
 */
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private static final String HEADER_PREFIX = "Bearer ";

    private final JwtParser jwtParser;

    public JwtFilter(String secretKey) {
        this.jwtParser = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("authorization");
        if (!StringUtils.hasText(authorization) || !authorization.startsWith(HEADER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        String accessToken = authorization.substring(HEADER_PREFIX.length());

        Jws<Claims> claimsJws;
        try {
            claimsJws = jwtParser.parseSignedClaims(accessToken);
        } catch (JwtException e) {
            log.warn("The access token cannot be parsed: {}, reason={}", accessToken, e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        Claims body = claimsJws.getPayload();
        String username = body.getSubject();
        Role role = Role.valueOf(body.get("role").toString());
        String sessionId = body.get("sessionId").toString();

        JwtUser jwtUser = new JwtUser(username, role, sessionId);

        List<GrantedAuthority> grantedAuthorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
        Authentication authentication = new UsernamePasswordAuthenticationToken(jwtUser, null, grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
