package com.github.nikitakuchur.userservice.exceptions;

/**
 * This exception is thrown if a user is trying to use a refresh token that has expired.
 */
public class RefreshTokenExpiredException extends RuntimeException {
    public RefreshTokenExpiredException(String message) {
        super(message);
    }
}
