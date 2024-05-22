package com.github.nikitakuchur.userservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * A representation of a login request.
 */
@Data
public class LoginRequest {
    @NotBlank(message = "A username cannot be empty")
    private String username;
    @NotBlank(message = "A password cannot be empty")
    private String password;
}
