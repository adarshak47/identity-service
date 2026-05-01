package com.adarsh.identity_service.auth.exception;

public class UserNotActiveException extends RuntimeException {
    public UserNotActiveException(String email) {
        super("Email not verified for user: " + email);
    }
}
