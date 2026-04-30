package com.adarsh.identity_service.common;

import com.adarsh.identity_service.auth.exception.EmailAlreadyExistsException;

import com.adarsh.identity_service.auth.exception.InvalidCredentialsException;
import com.adarsh.identity_service.common.response.ApiResponse;
import com.adarsh.identity_service.common.response.ErrorResponse;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleEmailExists(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse.failure(
                new ErrorResponse("EMAIL_ALREADY_EXISTS", ex.getMessage())
            ));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.failure(
                new ErrorResponse("INVALID_CREDENTIALS", ex.getMessage())
            ));
    }
}
