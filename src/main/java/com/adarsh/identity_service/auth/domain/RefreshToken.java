package com.adarsh.identity_service.auth.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "refresh_tokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    private UUID id;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "family_id", nullable = false)
    private UUID familyId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private UserAccount user;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private boolean revoked;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "replaced_by")
    private UUID replacedBy;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "reuse_detected")
    private boolean reuseDetected;

    @Transient
    private String rawToken;

    public RefreshToken(UUID id, String tokenHash, UUID familyId, UserAccount user, LocalDateTime expiryDate, boolean revoked, String deviceName, String ipAddress, String userAgent) {
        this.id = id;
        this.tokenHash = tokenHash;
        this.familyId = familyId;
        this.user = user;
        this.expiryDate = expiryDate;
        this.revoked = revoked;
        this.createdAt = LocalDateTime.now();
        this.deviceName = deviceName;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }

    public void revoke() {
        this.revoked = true;
        this.revokedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }

    public void markReuseDetected() {
        this.reuseDetected = true;
    }

    public void setReplacedBy(UUID id) {
        this.replacedBy = id;
    }

    public void setRawToken(String rawToken) {
        this.rawToken = rawToken;
    }

    public UUID getFamilyId() {
        return familyId;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }
}
