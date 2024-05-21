package com.github.nikitakuchur.chatmanager.exceptions;

/**
 * This exception is thrown when the user does not have permissions to delete the chat.
 */
public class UnauthorizedChatDeleteException extends RuntimeException {
    public UnauthorizedChatDeleteException(String message) {
        super(message);
    }
}
