package com.adarsh.identity_service.auth.exception;

import com.adarsh.identity_service.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class IncorrectOtpException extends BaseException {

    public IncorrectOtpException() {
        super("INCORRECT_OTP", "Incorrect OTP", HttpStatus.UNAUTHORIZED);
    }
}
