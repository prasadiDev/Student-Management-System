package com.usj.sms.exception;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handles validation errors (@Valid fails)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage()));

        // Returns 400 Bad Request with a structured JSON body of errors
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Handles scenarios where we try to delete an entity that doesn't exist
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<String> handleNotFoundException(EmptyResultDataAccessException ex, WebRequest request) {
        // Returns 404 Not Found
        return new ResponseEntity<>("Resource not found or already deleted.", HttpStatus.NOT_FOUND);
    }
}
