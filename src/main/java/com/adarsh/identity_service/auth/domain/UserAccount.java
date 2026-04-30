package com.adarsh.identity_service.auth.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Entity
@Table(name="users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAccount {

    @Id
    private UUID id;

    @Column(nullable=false, unique=true)
    private String email;

    @Column(name="password_hash", nullable=false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @CreationTimestamp
    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Column(name = "failed_attempts")
    private int failedAttempts;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    public UserAccount(UUID id, String email, String passwordHash, UserStatus status){
        this.id=id;
        this.email=email;
        this.passwordHash=passwordHash;
        this.status=status;
    }

    public void addRole(Role role){
        this.roles.add(role);
    }

    public void incrementFailedAttempts(int maxAttempts, int lockMinutes) {
        this.failedAttempts++;

        if (this.failedAttempts >= maxAttempts) {
            this.lockedUntil = LocalDateTime.now().plusMinutes(lockMinutes);
        }
    }

    public void resetFailedAttempts() {
        this.failedAttempts = 0;
        this.lockedUntil = null;
    }

    public boolean isAccountLocked() {
        return this.lockedUntil != null &&
            this.lockedUntil.isAfter(LocalDateTime.now());
    }
}
