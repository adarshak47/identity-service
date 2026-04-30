package com.adarsh.identity_service.auth.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name="refresh_tokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    private UUID id;

    @Column(nullable=false, unique=true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private UserAccount user;

    @Column(name="expiry_date", nullable=false)
    private LocalDateTime expiryDate;

    @Column(nullable=false)
    private boolean revoked;

    @CreationTimestamp
    @Column(name="created_at")
    private LocalDateTime createdAt;

    public RefreshToken(UUID id, String token, UserAccount user, LocalDateTime expiryDate, boolean revoked){
        this.id=id;
        this.token=token;
        this.user=user;
        this.expiryDate=expiryDate;
        this.revoked=revoked;
    }

    public void revoke(){
        this.revoked=true;
    }

    public boolean isExpired(){
        return expiryDate.isBefore(LocalDateTime.now());
    }
}
