package com.adarsh.identity_service.auth.exception;

import com.adarsh.identity_service.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidVerificationTokenException extends BaseException {

    public InvalidVerificationTokenException(String message) {
        super("INVALID_VERIFICATION_TOKEN", message, HttpStatus.BAD_REQUEST);
    }
}
