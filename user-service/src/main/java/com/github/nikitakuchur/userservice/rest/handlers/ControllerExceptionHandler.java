package com.github.nikitakuchur.userservice.rest.handlers;

import com.github.nikitakuchur.userservice.exceptions.RefreshTokenExpiredException;
import com.github.nikitakuchur.userservice.exceptions.RefreshTokenNotFoundException;
import com.github.nikitakuchur.userservice.exceptions.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Optional;
import java.util.stream.Collectors;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    protected ResponseEntity<Object> handleUserAlreadyExists(UserAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler({
            BadCredentialsException.class,
            RefreshTokenNotFoundException.class,
            RefreshTokenExpiredException.class
    })
    protected ResponseEntity<Object> handleUserAlreadyExists(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler({UsernameNotFoundException.class})
    protected ResponseEntity<Object> handleUserAlreadyExists(UsernameNotFoundException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(BindingResult result) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(result.getFieldErrors().stream()
                        .collect(Collectors.toMap(FieldError::getField, this::getErrorMessage, (a, b) -> a)));
    }

    private String getErrorMessage(FieldError error) {
        return Optional.ofNullable(error.getDefaultMessage())
                .orElse("invalid");
    }
}
