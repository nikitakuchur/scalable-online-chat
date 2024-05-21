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
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = "^\\S*$", message = "The username cannot contain any whitespaces")
    private String username;
    @NotBlank
    @Length(min = 8)
    private String password;
}
