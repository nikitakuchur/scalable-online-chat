package com.github.nikitakuchur.userservice.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * A representation of a signup request.
 */
@Data
public class SignupRequest {
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Please enter a valid email")
    private String email;
    @NotBlank(message = "A username cannot be empty")
    @Pattern(regexp = "^\\S*$", message = "A username cannot contain any whitespace characters")
    private String username;
    @Length(min = 8, message = "A password must be at least 8 characters in length")
    private String password;
}
