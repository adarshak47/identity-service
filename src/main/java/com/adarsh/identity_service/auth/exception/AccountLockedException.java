package com.adarsh.identity_service.auth.exception;

public class AccountLockedException extends RuntimeException {

    public AccountLockedException() {
        super("Account is temporarily locked due to multiple failed login attempts");
    }
}
