package com.adarsh.identity_service.auth.exception;

import com.adarsh.identity_service.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RateLimitExceededException extends BaseException {

    public RateLimitExceededException() {
        super("RATE_LIMIT_EXCEEDED", "Too many requests. Please try again later.", HttpStatus.TOO_MANY_REQUESTS);
    }
}
