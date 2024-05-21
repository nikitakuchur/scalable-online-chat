package com.github.nikitakuchur.chatmanager.models.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * A representation of a create chat request.
 */
@Data
public class ChatInfoRequest {
    @NotBlank
    private String name;
    private String description;
}
