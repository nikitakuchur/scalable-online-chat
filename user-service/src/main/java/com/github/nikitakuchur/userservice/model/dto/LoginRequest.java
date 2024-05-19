package com.github.nikitakuchur.userservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * A representation of a login request.
 */
@Data
public class LoginRequest {
    @NotBlank
    private String username;
    @NotBlank
    @Length(min = 8)
    private String password;
}
