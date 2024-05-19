package com.github.nikitakuchur.userservice.exceptions;

/**
 * This exception is thrown if a user is trying to use a refresh token that has been revoked.
 */
public class RefreshTokenRevokedException extends RuntimeException {
    public RefreshTokenRevokedException(String message) {
        super(message);
    }
}
