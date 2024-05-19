package com.github.nikitakuchur.userservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * A representation of a refresh token request.
 */
@Data
public class RefreshTokenRequest {
    @NotBlank
    private String refreshToken;
}
