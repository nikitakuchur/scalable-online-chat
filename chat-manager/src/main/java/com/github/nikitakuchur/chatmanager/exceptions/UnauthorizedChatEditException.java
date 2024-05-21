package com.github.nikitakuchur.chatmanager.exceptions;

/**
 * This exception is thrown when the user does not have permissions to edit the chat.
 */
public class UnauthorizedChatEditException extends RuntimeException {
    public UnauthorizedChatEditException(String message) {
        super(message);
    }
}
