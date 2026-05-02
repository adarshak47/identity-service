package com.adarsh.identity_service.auth.exception;

import com.adarsh.identity_service.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserNotActiveException extends BaseException {

    public UserNotActiveException(String email) {
        super("USER_NOT_ACTIVE", "Email not verified for user: " + email, HttpStatus.FORBIDDEN);
    }
}
