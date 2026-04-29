package com.adarsh.identity_service.auth.repository;

import com.adarsh.identity_service.auth.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID>{

    Optional<RefreshToken> findByToken(String token);
}
