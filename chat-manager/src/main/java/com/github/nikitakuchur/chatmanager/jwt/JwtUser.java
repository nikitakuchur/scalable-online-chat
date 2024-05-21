package com.github.nikitakuchur.chatmanager.jwt;

/**
 * User information stored in a JWT token.
 *
 * @param username  the username
 * @param role      the role
 * @param sessionId the session ID
 */
public record JwtUser(
        String username,
        String role,
        String sessionId
) {
}
