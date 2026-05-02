package com.adarsh.identity_service.auth.exception;

import com.adarsh.identity_service.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends BaseException {

    public EmailAlreadyExistsException(String email) {
        super("EMAIL_ALREADY_EXISTS", "Email already registered: " + email, HttpStatus.CONFLICT);
    }
}
