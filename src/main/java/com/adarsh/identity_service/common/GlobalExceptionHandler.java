package com.adarsh.identity_service.common;

import com.adarsh.identity_service.auth.exception.EmailAlreadyExistsException;

import com.adarsh.identity_service.auth.exception.InvalidCredentialsException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String,String>> handleDuplicateEmail(EmailAlreadyExistsException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String,String>> handleInvalidCredentials(InvalidCredentialsException ex){
        return ResponseEntity.status(401).body(Map.of("error", ex.getMessage()));
    }
}
