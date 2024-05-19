package com.github.nikitakuchur.userservice.exceptions;

/**
 * This exception is thrown if a user cannot be created because it already exists.
 */
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
