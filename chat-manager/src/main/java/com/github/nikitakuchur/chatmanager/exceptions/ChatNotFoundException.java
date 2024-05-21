package com.github.nikitakuchur.chatmanager.exceptions;

/**
 * This exception is thrown when the application is unable to find the required chat.
 */
public class ChatNotFoundException extends RuntimeException {
    public ChatNotFoundException(String message) {
        super(message);
    }
}
