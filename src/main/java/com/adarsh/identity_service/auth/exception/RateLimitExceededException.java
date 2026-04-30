package com.adarsh.identity_service.auth.exception;

public class RateLimitExceededException extends RuntimeException {

    public RateLimitExceededException(String message) {
        super(message);
    }

    public RateLimitExceededException() {
        super("Too many requests. Please try again later.");
    }
}
