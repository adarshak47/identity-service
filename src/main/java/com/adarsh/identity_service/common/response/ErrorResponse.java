package com.adarsh.identity_service.common.response;

import lombok.Getter;

import java.util.List;

@Getter
public class ErrorResponse {

    private final String code;
    private final String message;
    private final List<FieldError> details;

    public ErrorResponse(String code, String message) {
        this(code, message, null);
    }

    public ErrorResponse(String code, String message, List<FieldError> details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }
}
