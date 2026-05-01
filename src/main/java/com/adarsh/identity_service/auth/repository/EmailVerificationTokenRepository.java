package com.adarsh.identity_service.auth.repository;

import com.adarsh.identity_service.auth.domain.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, java.util.UUID> {
    Optional<EmailVerificationToken> findByToken(String token);
    void deleteAllByUser_Id(java.util.UUID userId);
}
