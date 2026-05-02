package com.adarsh.identity_service.user.dto;

import java.time.LocalDateTime;

public record DeviceSessionResponse(
    String sessionId,
    String deviceName,
    String ipAddress,
    String userAgent,
    boolean revoked,
    LocalDateTime createdAt
) {}
