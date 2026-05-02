package com.adarsh.identity_service.auth.exception;

import com.adarsh.identity_service.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AccountLockedException extends BaseException {

    public AccountLockedException() {
        super("ACCOUNT_LOCKED", "Account is temporarily locked due to multiple failed login attempts", HttpStatus.LOCKED);
    }
}
