package com.github.nikitakuchur.userservice.jwt;

import com.github.nikitakuchur.userservice.model.Role;

/**
 * User information stored in a JWT token.
 *
 * @param username  the username
 * @param role      the role
 * @param sessionId the session ID
 */
public record JwtUser(
        String username,
        Role role,
        String sessionId
) {
}