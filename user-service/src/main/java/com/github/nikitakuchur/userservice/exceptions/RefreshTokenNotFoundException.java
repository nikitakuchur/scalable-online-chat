package com.github.nikitakuchur.userservice.exceptions;

/**
 * This exception is thrown if a user is trying to use a non-existent refresh token.
 */
public class RefreshTokenNotFoundException extends RuntimeException {
    public RefreshTokenNotFoundException(String message) {
        super(message);
    }
}
