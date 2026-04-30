package com.adarsh.identity_service.auth.service;

import com.adarsh.identity_service.auth.domain.*;
import com.adarsh.identity_service.auth.repository.
    RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final
    RefreshTokenRepository repository;

    public RefreshToken create(UserAccount user){

        RefreshToken token = new RefreshToken(UUID.randomUUID(), generateSecureToken(), user, LocalDateTime.now().plusDays(7), false);
        return repository.save(token);
    }

    private String generateSecureToken(){
        byte[] random = new byte[64];
        new SecureRandom().nextBytes(random);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(random);
    }
}
