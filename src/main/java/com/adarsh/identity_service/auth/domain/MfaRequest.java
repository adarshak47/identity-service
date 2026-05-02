package com.adarsh.identity_service.auth.domain;

public record MfaRequest(
    String email,
    String otp
) {}
