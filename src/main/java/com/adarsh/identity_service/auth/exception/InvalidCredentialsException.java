package com.adarsh.identity_service.auth.exception;

import com.adarsh.identity_service.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends BaseException {

    public InvalidCredentialsException() {
        super("INVALID_CREDENTIALS", "Invalid credentials", HttpStatus.UNAUTHORIZED);
    }
}
