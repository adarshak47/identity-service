package com.adarsh.identity_service.auth.service;



import com.adarsh.identity_service.auth.domain.*;
import com.adarsh.identity_service.auth.dto.*;
import com.adarsh.identity_service.auth.exception.EmailAlreadyExistsException;
import com.adarsh.identity_service.auth.exception.InvalidCredentialsException;
import com.adarsh.identity_service.auth.exception.RateLimitExceededException;
import com.adarsh.identity_service.auth.repository.RefreshTokenRepository;
import com.adarsh.identity_service.auth.repository.RoleRepository;
import com.adarsh.identity_service.auth.repository.UserAccountRepository;
import com.adarsh.identity_service.common.security.TokenHashUtil;
import com.adarsh.identity_service.common.web.RequestContext;
import com.adarsh.identity_service.security.jwt.JwtTokenProvider;
import com.adarsh.identity_service.security.ratelimit.RateLimiterService;
import com.adarsh.identity_service.user.dto.SessionResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserAccountRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RoleRepository roleRepository;
    private final RequestContext requestContext;
    private final RateLimiterService rateLimiterService;

    public RegisterResponse registerUser(RegisterRequest request) {

        if(repository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }
        UserAccount user = new UserAccount(UUID.randomUUID(), request.email(), passwordEncoder.encode(request.password()), UserStatus.ACTIVE);
        Role userRole = roleRepository.findByName("USER").orElseThrow();

        user.addRole(userRole);
        UserAccount savedUser = repository.save(user);
        return new RegisterResponse(savedUser.getId(), savedUser.getEmail(), "User registered successfully");
    }

    public LoginResponse login(LoginRequest request) {

        String key = requestContext.getClientIp() + ":" + request.email();

        if (!rateLimiterService.isAllowed(key)) {
            throw new RateLimitExceededException(
                "Too many login attempts for " + key
            );
        }

        UserAccount user = repository.findByEmail(request.email())
            .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        rateLimiterService.reset(key); // reset on success

        List<String> roles = user.getRoles().stream().map(Role::getName).toList();

        List<String> permissions = user.getRoles()
            .stream()
            .flatMap(r -> r.getPermissions().stream())
            .map(Permission::getName)
            .distinct()
            .toList();

        String accessToken = jwtTokenProvider.generateToken(
            user.getId().toString(),
            user.getEmail(),
            roles,
            permissions
        );

        RefreshToken refreshToken = refreshTokenService.create(
            user,
            request.deviceName(),
            requestContext.getClientIp(),
            requestContext.getUserAgent()
        );

        return new LoginResponse(accessToken, refreshToken.getRawToken(), "Bearer");
    }
    public LoginResponse refreshToken(RefreshRequest request) {

        String tokenHash = TokenHashUtil.hash(request.refreshToken());
        RefreshToken existingToken = refreshTokenRepository.findByTokenHash(tokenHash).orElseThrow(InvalidCredentialsException::new);
        if (existingToken.isRevoked() || existingToken.isExpired()) {
            throw new InvalidCredentialsException();
        }

        existingToken.revoke();

        refreshTokenRepository.save(existingToken);

        UserAccount user = existingToken.getUser();

        List<String> roles = user.getRoles().stream().map(Role::getName).toList();
        List<String> permissions = user.getRoles()
            .stream()
            .flatMap(role -> role.getPermissions().stream())
            .map(Permission::getName)
            .distinct()
            .toList();

        String newAccessToken = jwtTokenProvider.generateToken(
            user.getId().toString(),
            user.getEmail(),
            roles,
            permissions
        );
        RefreshToken newRefreshToken = refreshTokenService.create(
            user,
            existingToken.getDeviceName(),
            existingToken.getIpAddress(),
            existingToken.getUserAgent()
        );

        return new LoginResponse(newAccessToken, newRefreshToken.getRawToken(), "Bearer");
    }

    public void logout(LogoutRequest request) {
        String tokenHash = TokenHashUtil.hash(request.refreshToken());

        refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(token -> {
            if (!token.isRevoked() && !token.isExpired()) {
                token.revoke();
                refreshTokenRepository.save(token);
            }
        });
    }

    public List<SessionResponse> getUserSessions(String userId) {

        UserAccount user = repository.findById(UUID.fromString(userId))
            .orElseThrow(() -> new InvalidCredentialsException());

        return refreshTokenRepository.findByUser(user)
            .stream()
            .map(token -> new SessionResponse(
                token.getDeviceName(),
                token.getIpAddress(),
                token.getUserAgent(),
                token.isRevoked()
            ))
            .toList();
    }

}
