package com.adarsh.identity_service.auth.service;

import com.adarsh.identity_service.auth.domain.RefreshToken;
import com.adarsh.identity_service.auth.domain.UserAccount;
import com.adarsh.identity_service.auth.repository.RefreshTokenRepository;
import com.adarsh.identity_service.common.security.TokenHashUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    @Value("${security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public RefreshToken create(UserAccount user, String deviceName, String ipAddress, String userAgent) {

        String rawToken = generateSecureToken();
        String tokenHash = TokenHashUtil.hash(rawToken);

        UUID familyId = UUID.randomUUID();

        RefreshToken token = new RefreshToken(UUID.randomUUID(), tokenHash, familyId, user, LocalDateTime.now().plusSeconds(refreshTokenExpiration), false, deviceName, ipAddress, userAgent);

        repository.save(token);
        token.setRawToken(rawToken);

        return token;
    }

    private String generateSecureToken() {
        byte[] random = new byte[64];
        new SecureRandom().nextBytes(random);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(random);
    }
}
