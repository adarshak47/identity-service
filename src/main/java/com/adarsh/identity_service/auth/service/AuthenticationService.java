package com.adarsh.identity_service.auth.service;



import com.adarsh.identity_service.auth.domain.RefreshToken;
import com.adarsh.identity_service.auth.domain.UserAccount;
import com.adarsh.identity_service.auth.domain.UserStatus;
import com.adarsh.identity_service.auth.dto.*;
import com.adarsh.identity_service.auth.exception.EmailAlreadyExistsException;
import com.adarsh.identity_service.auth.exception.InvalidCredentialsException;
import com.adarsh.identity_service.auth.repository.RefreshTokenRepository;
import com.adarsh.identity_service.auth.repository.UserAccountRepository;
import com.adarsh.identity_service.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserAccountRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public RegisterResponse registerUser(RegisterRequest request) {

        if(repository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }
        UserAccount user = new UserAccount(UUID.randomUUID(), request.email(), passwordEncoder.encode(request.password()), UserStatus.ACTIVE);
        UserAccount savedUser = repository.save(user);
        return new RegisterResponse(savedUser.getId(), savedUser.getEmail(), "User registered successfully");
    }

    public LoginResponse login(LoginRequest request) {

        UserAccount user = repository.findByEmail(request.email()).orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtTokenProvider.generateToken(user.getId().toString(), user.getEmail());

        RefreshToken refreshToken = refreshTokenService.create(user);

        return new LoginResponse(accessToken, refreshToken.getToken(), "Bearer");
    }

    public LoginResponse refreshToken(RefreshRequest request) {

        RefreshToken existingToken = refreshTokenRepository.findByToken(request.refreshToken()).orElseThrow(InvalidCredentialsException::new);

        if (existingToken.isRevoked() || existingToken.isExpired()) {
            throw new InvalidCredentialsException();
        }

        existingToken.revoke();

        refreshTokenRepository.save(existingToken);

        UserAccount user = existingToken.getUser();

        String newAccessToken = jwtTokenProvider.generateToken(user.getId().toString(), user.getEmail());

        RefreshToken newRefreshToken = refreshTokenService.create(user);

        return new LoginResponse(newAccessToken, newRefreshToken.getToken(), "Bearer");
    }

    public void logout(LogoutRequest request){

        RefreshToken token = refreshTokenRepository.findByToken(request.refreshToken()).orElseThrow(InvalidCredentialsException::new);
        token.revoke();
        refreshTokenRepository.save(token);
    }
}
