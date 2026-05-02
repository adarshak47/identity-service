package com.adarsh.identity_service.common;

import com.adarsh.identity_service.common.exception.BaseException;
import com.adarsh.identity_service.common.response.*;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException ex) {

        ErrorResponse error = new ErrorResponse(ex.getCode(), ex.getMessage());

        return ResponseEntity.status(ex.getStatus()).body(ApiResponse.failure(error));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(MethodArgumentNotValidException ex) {

        List<FieldError> errors = ex.getBindingResult().getFieldErrors().stream().map(err -> new FieldError(err.getField(), err.getDefaultMessage())).toList();

        ErrorResponse errorResponse = new ErrorResponse("VALIDATION_ERROR", "Invalid request", errors);

        return ResponseEntity.badRequest().body(ApiResponse.failure(errorResponse));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {

        ErrorResponse error = new ErrorResponse("INTERNAL_SERVER_ERROR", "Something went wrong");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.failure(error));
    }
}
