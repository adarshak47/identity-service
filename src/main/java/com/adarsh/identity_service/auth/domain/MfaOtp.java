package com.adarsh.identity_service.auth.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "mfa_otp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MfaOtp {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserAccount user;

    private String otpHash;

    private LocalDateTime expiresAt;

    private int attempts = 0;
}
