package com.adarsh.identity_service.audit.event;

import java.time.LocalDateTime;

public record AuditEvent(
    String action,
    String actor,
    String details,
    String remoteIp,
    LocalDateTime timestamp
) {}
