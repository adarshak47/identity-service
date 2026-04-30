package com.adarsh.identity_service.auth.service;

import com.adarsh.identity_service.auth.domain.RefreshToken;
import com.adarsh.identity_service.auth.domain.UserAccount;
import com.adarsh.identity_service.auth.repository.RefreshTokenRepository;
import com.adarsh.identity_service.common.security.TokenHashUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    public RefreshToken create(UserAccount user) {
        String rawToken = generateSecureToken();
        String tokenHash = TokenHashUtil.hash(rawToken);

        RefreshToken token = new RefreshToken(UUID.randomUUID(), tokenHash, user, LocalDateTime.now().plusDays(7), false);

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
