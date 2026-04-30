package com.adarsh.identity_service.auth.domain;

import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private UserAccount user;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private boolean revoked;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Transient
    private String rawToken;

    public RefreshToken(UUID id, String tokenHash, UserAccount user, LocalDateTime expiryDate, boolean revoked) {
        this.id = id;
        this.tokenHash = tokenHash;
        this.user = user;
        this.expiryDate = expiryDate;
        this.revoked = revoked;
        this.createdAt = LocalDateTime.now();
    }

    public void revoke() {
        this.revoked = true;
    }

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }

    public void setRawToken(String rawToken) {
        this.rawToken = rawToken;
    }
}
