package com.adarsh.identity_service.auth.dto;

public record LogoutRequest(
    String refreshToken
){}
