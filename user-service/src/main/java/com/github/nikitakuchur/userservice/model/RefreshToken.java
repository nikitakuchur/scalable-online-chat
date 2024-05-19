package com.github.nikitakuchur.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;

/**
 * A representation of a MongoDB document that
 * stores information about a refresh token.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("refreshTokens")
public class RefreshToken {
    @MongoId
    private String id;
    private String username;
    private String token;
    private String sessionId;
    private Instant expiration;
    private boolean invalidated;
}
