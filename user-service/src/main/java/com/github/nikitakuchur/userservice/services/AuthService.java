package com.github.nikitakuchur.userservice.services;

import com.github.nikitakuchur.userservice.exceptions.RefreshTokenExpiredException;
import com.github.nikitakuchur.userservice.exceptions.RefreshTokenNotFoundException;
import com.github.nikitakuchur.userservice.jwt.TokenPair;
import com.github.nikitakuchur.userservice.model.RefreshToken;
import com.github.nikitakuchur.userservice.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * A service that is responsible for authentication and authorisation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    @Value("${user-service.jwt.secret-key}")
    private String secretKey;

    @Value("${user-service.jwt.access-token.lifetime}")
    private long accessTokenLifetime;

    @Value("${user-service.jwt.refresh-token.lifetime}")
    private long refreshTokenLifetime;

    /**
     * Authenticates the given username and password, generating a new access and refresh token.
     *
     * @param username the username to authenticate
     * @param password the password to authenticate
     * @return a pair of tokens: an access token and a refresh token
     * @throws BadCredentialsException if the combination of username and password is incorrect
     */
    public TokenPair authenticate(String username, String password) {
        User user;
        try {
            Authentication aut = authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            user = (User) aut.getPrincipal();
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Incorrect username or password");
        }

        String sessionId = UUID.randomUUID().toString();
        Instant timestamp = Instant.now();
        String accessToken = createAccessToken(user, sessionId, timestamp);
        String refreshToken = createRefreshToken(user, sessionId, timestamp);

        return new TokenPair(accessToken, refreshToken);
    }

    private String createAccessToken(User user, String sessionId, Instant timestamp) {
        Date createdAt = new Date(timestamp.toEpochMilli());
        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .subject(user.getUsername())
                .claim("sessionId", sessionId)
                .claim("role", user.getRole())
                .issuedAt(createdAt)
                .notBefore(createdAt)
                .expiration(new Date(createdAt.getTime() + Duration.ofMinutes(accessTokenLifetime).toMillis()))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    private String createRefreshToken(User user, String sessionId, Instant timestamp) {
        RefreshToken refreshToken = RefreshToken.builder()
                .username(user.getUsername())
                .token(UUID.randomUUID().toString().replace("-", ""))
                .sessionId(sessionId)
                .expiration(timestamp.plus(refreshTokenLifetime, ChronoUnit.MINUTES))
                .build();
        refreshTokenService.save(refreshToken);
        return refreshToken.getToken();
    }

    /**
     * Generates a new pair of tokens based on the given refresh token.
     * The given refresh token will be invalidated after executing this method.
     *
     * @param refreshToken the refresh token
     * @return a new pair of tokens: an access token and a refresh token
     * @throws RefreshTokenNotFoundException if the refresh token does not exist
     * @throws UsernameNotFoundException if the user that owns the token does not exist
     * @throws RefreshTokenExpiredException if the given refresh token has expired
     */
    public TokenPair refreshToken(String refreshToken) {
        return refreshTokenService.findByToken(refreshToken)
                .map(token -> {
                    String sessionId = token.getSessionId();
                    Instant timestamp = Instant.now();

                    verifyRefreshToken(token);
                    refreshTokenService.revoke(token);

                    String username = token.getUsername();
                    return userService.findByUsername(username)
                            .map(user -> {
                                String newAccessToken = createAccessToken(user, sessionId, timestamp);
                                String newRefreshToken = createRefreshToken(user, sessionId, timestamp);
                                return new TokenPair(newAccessToken, newRefreshToken);
                            }).orElseThrow(() -> new UsernameNotFoundException("User not found. Username=" + username));
                }).orElseThrow(() -> new RefreshTokenNotFoundException("Refresh token not found."));
    }

    private void verifyRefreshToken(RefreshToken token) {
        if (isTokenExpired(token)) {
            log.warn("Refresh token expired. Username={}", token.getUsername());
            refreshTokenService.revoke(token);
            throw new RefreshTokenExpiredException("The refresh token has expired. Please, log in again.");
        }
    }

    /**
     * Logs out the user from the current session.
     *
     * @param sessionId the session ID
     */
    public void logout(String sessionId) {
        refreshTokenService.revokeAllBySessionId(sessionId);
    }

    /**
     * Retrieves a list of all active session IDs associated with the given username.
     *
     * @param username the username
     * @return a list of session IDs
     */
    public List<String> getSessions(String username) {
        return refreshTokenService.findByUsername(username).stream()
                .filter(t -> !isTokenExpired(t))
                .map(RefreshToken::getSessionId)
                .distinct()
                .toList();
    }

    private boolean isTokenExpired(RefreshToken token) {
        return token.getExpiration().compareTo(Instant.now()) < 0;
    }

    /**
     * Kills the given session.
     *
     * @param sessionId the session ID
     */
    public void killSession(String sessionId) {
        refreshTokenService.revokeAllBySessionId(sessionId);
    }

    /**
     * Kills all sessions associated with the given username.
     *
     * @param username the username
     */
    public void killAllSessions(String username) {
        refreshTokenService.revokeAllByUsername(username);
    }
}
