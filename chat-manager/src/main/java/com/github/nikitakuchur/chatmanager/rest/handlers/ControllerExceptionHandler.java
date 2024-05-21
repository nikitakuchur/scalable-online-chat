package com.github.nikitakuchur.chatmanager.rest.handlers;

import com.github.nikitakuchur.chatmanager.exceptions.ChatNotFoundException;
import com.github.nikitakuchur.chatmanager.exceptions.UnauthorizedChatDeleteException;
import com.github.nikitakuchur.chatmanager.exceptions.UnauthorizedChatEditException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Optional;
import java.util.stream.Collectors;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ChatNotFoundException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(ChatNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler({UnauthorizedChatEditException.class, UnauthorizedChatDeleteException.class})
    protected ResponseEntity<Object> handleMethodArgumentNotValid(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(BindingResult result) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(result.getFieldErrors().stream()
                        .collect(Collectors.toMap(FieldError::getField, this::getErrorMessage)));
    }

    private String getErrorMessage(FieldError error) {
        return Optional.ofNullable(error.getDefaultMessage())
                .orElse("invalid");
    }
}
