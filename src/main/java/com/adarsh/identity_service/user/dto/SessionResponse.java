package com.adarsh.identity_service.user.dto;

public record SessionResponse(
    String deviceName,
    String ipAddress,
    String userAgent,
    boolean revoked
) {}
