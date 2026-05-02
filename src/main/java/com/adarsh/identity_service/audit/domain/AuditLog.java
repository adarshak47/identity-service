package com.adarsh.identity_service.audit.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "audit_logs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditLog {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String action; // e.g., REGISTER, LOGIN, LOGIN_FAIL, EMAIL_VERIFY

    @Column(nullable = false)
    private String actor; // Can be user email, userId, or "system"

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(length = 1000)
    private String details; // Anything serializable

    @Column(nullable = false)
    private String remoteIp;

    public AuditLog(String action, String actor, String details, String remoteIp) {
        this.id = UUID.randomUUID();
        this.action = action;
        this.actor = actor != null ? actor : "anonymous";
        this.timestamp = LocalDateTime.now();
        this.details = details;
        this.remoteIp = remoteIp != null ? remoteIp : "unknown";
    }
}
