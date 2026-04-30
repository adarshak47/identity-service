package com.adarsh.identity_service.common;

import com.adarsh.identity_service.auth.exception.EmailAlreadyExistsException;

import com.adarsh.identity_service.auth.exception.InvalidCredentialsException;
import com.adarsh.identity_service.common.response.ApiResponse;
import com.adarsh.identity_service.common.response.ErrorResponse;
import com.adarsh.identity_service.common.response.FieldError;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(MethodArgumentNotValidException ex) {

        List<FieldError> errors = ex.getBindingResult().getFieldErrors().stream().map(err -> new FieldError(
                    err.getField(),
                    err.getDefaultMessage()
                )).toList();

        ErrorResponse errorResponse = new ErrorResponse("VALIDATION_ERROR", "Invalid request", errors);

        return ResponseEntity.badRequest().body(ApiResponse.failure(errorResponse));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {

        ErrorResponse error = new ErrorResponse("INTERNAL_SERVER_ERROR", "Something went wrong");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.failure(error));
    }
}
