package com.adarsh.identity_service.auth.repository;

import com.adarsh.identity_service.auth.domain.RefreshToken;
import com.adarsh.identity_service.auth.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findByUser(UserAccount user);
}
