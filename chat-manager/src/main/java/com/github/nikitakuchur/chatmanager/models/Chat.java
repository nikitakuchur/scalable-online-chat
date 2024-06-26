package com.github.nikitakuchur.chatmanager.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 * A representation of a MongoDB document that
 * stores information about a chat.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("chats")
public class Chat {
    @MongoId
    private String id;

    @TextIndexed(weight = 2)
    private String name;
    @TextIndexed
    private String description;

    private String owner;
}
