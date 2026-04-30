package com.adarsh.identity_service.auth.dto;

public record LoginResponse(
    String accessToken,
    String refreshToken,
    String tokenType
) {
}
