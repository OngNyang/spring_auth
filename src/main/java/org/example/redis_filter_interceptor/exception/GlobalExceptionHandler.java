package org.example.redis_filter_interceptor.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?>    handleInvalidCredentials(InvalidCredentialsException e) {

        return (ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?>    handleException(Exception e) {

        return (ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage()));
    }
}
