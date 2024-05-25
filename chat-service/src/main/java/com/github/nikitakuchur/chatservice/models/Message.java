package com.github.nikitakuchur.chatservice.models;

import java.time.Instant;

public record Message(
        String id,
        String chatId,
        String sender,
        String text,
        Instant timestamp,
        MessageType type
) {
}
