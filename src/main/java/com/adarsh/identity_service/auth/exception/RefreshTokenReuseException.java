package com.adarsh.identity_service.auth.exception;

import com.adarsh.identity_service.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RefreshTokenReuseException extends BaseException {

    public RefreshTokenReuseException() {
        super("REFRESH_TOKEN_REUSE", "Refresh token reuse detected. Session revoked.", HttpStatus.UNAUTHORIZED);
    }
}
