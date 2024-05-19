package com.github.nikitakuchur.userservice.jwt;

/**
 * A pair that contains an access token and the corresponding refresh token.
 *
 * @param accessToken   the access token
 * @param refreshToken  the refresh token
 */
public record TokenPair(
        String accessToken,
        String refreshToken
) {
}
