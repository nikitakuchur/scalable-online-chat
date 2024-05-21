package com.github.nikitakuchur.chatmanager.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;

/**
 * A representation of a MongoDB document that
 * stores information about a message.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("messages")
public class Message {
    @MongoId
    private String id;
    private String sender;
    private String chatId;
    private String text;
    private Instant timestamp;
}
