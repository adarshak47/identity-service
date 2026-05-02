package com.adarsh.identity_service.auth.service;


import com.adarsh.identity_service.audit.service.AuditLogService;
import com.adarsh.identity_service.auth.domain.*;
import com.adarsh.identity_service.auth.dto.*;
import com.adarsh.identity_service.auth.exception.*;
import com.adarsh.identity_service.auth.repository.RefreshTokenRepository;
import com.adarsh.identity_service.auth.repository.RoleRepository;
import com.adarsh.identity_service.auth.repository.UserAccountRepository;
import com.adarsh.identity_service.common.security.TokenHashUtil;
import com.adarsh.identity_service.common.util.InputNormalizer;
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
    private final EmailVerificationService emailVerificationService;
    private final AuditLogService auditLogService;

    public RegisterResponse registerUser(RegisterRequest request) {
        String ip = requestContext.getClientIp();
        String email = InputNormalizer.normalizeEmail(request.email());
        if (repository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        UserAccount user = new UserAccount(UUID.randomUUID(), email, passwordEncoder.encode(request.password()), UserStatus.INACTIVE);
        Role userRole = roleRepository.findByName("USER").orElseThrow();

        user.addRole(userRole);
        UserAccount savedUser = repository.save(user);
        emailVerificationService.sendVerificationEmail(user);
        auditLogService.log("REGISTER", user.getEmail(), "User registered", ip);
        return new RegisterResponse(savedUser.getId(), savedUser.getEmail(), "User registered successfully");
    }

    public LoginResponse login(LoginRequest request) {
        String ip = requestContext.getClientIp();
        try {
            String email = InputNormalizer.normalizeEmail(request.email());

            String rateLimitKey = requestContext.getClientIp() + ":" + email;

            if (!rateLimiterService.isAllowed(rateLimitKey)) {
                throw new RateLimitExceededException("Too many login attempts");
            }

            UserAccount user = repository.findByEmail(email).orElseThrow(InvalidCredentialsException::new);

            // 🔐 CHECK ACCOUNT LOCK
            if (user.isAccountLocked()) {
                throw new AccountLockedException();
            }

            if (!user.isActive()) {
                throw new UserNotActiveException(user.getEmail());
            }

            // ❌ WRONG PASSWORD
            if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {

                user.incrementFailedAttempts(2, 1); // 5 attempts → lock 10 min
                repository.save(user);

                throw new InvalidCredentialsException();
            }

            // ✅ SUCCESS
            user.resetFailedAttempts();
            repository.save(user);

            rateLimiterService.reset(rateLimitKey);

            List<String> roles = user.getRoles().stream().map(Role::getName).toList();

            List<String> permissions = user.getRoles().stream().flatMap(r -> r.getPermissions().stream()).map(Permission::getName).distinct().toList();

            String accessToken = jwtTokenProvider.generateToken(user.getId().toString(), user.getEmail(), roles, permissions);

            RefreshToken refreshToken = refreshTokenService.create(user, request.deviceName(), requestContext.getClientIp(), requestContext.getUserAgent());

            return new LoginResponse(accessToken, refreshToken.getRawToken(), "Bearer");
        } catch (InvalidCredentialsException e) {
            // Also log failed
            auditLogService.log("LOGIN_FAIL", request.email(), "Login failed: invalid credentials", ip);
            throw e;
        } catch (AccountLockedException e) {
            auditLogService.log("LOGIN_FAIL", request.email(), "Login failed: account locked", ip);
            throw e;
        } catch (RateLimitExceededException e) {
            auditLogService.log("LOGIN_FAIL", request.email(), "Login failed: Too many login attempts.", ip);
            throw e;
        } catch (UserNotActiveException e) {
            auditLogService.log("LOGIN_FAIL", request.email(), "Login failed: Email not verified", ip);
            throw e;
        }
    }

    public LoginResponse refreshToken(RefreshRequest request) {

        String tokenHash = TokenHashUtil.hash(request.refreshToken());

        RefreshToken existingToken = refreshTokenRepository.findByTokenHash(tokenHash).orElseThrow(InvalidCredentialsException::new);

        // ❌ If token is invalid or expired
        if (existingToken.isRevoked() || existingToken.isExpired()) {
            throw new InvalidCredentialsException();
        }

        UUID familyId = existingToken.getFamilyId();

        // 🚨 STEP 1: Detect reuse (VERY IMPORTANT)
        boolean reusedTokenExists = refreshTokenRepository.findByFamilyIdAndRevokedFalse(familyId).stream().anyMatch(t -> !t.getTokenHash().equals(tokenHash));

        if (reusedTokenExists) {

            // 🚨 SECURITY BREACH: revoke entire family
            refreshTokenRepository.findByFamilyId(familyId).forEach(token -> {
                token.revoke();
                token.markReuseDetected();
            });

            refreshTokenRepository.flush();

            throw new RuntimeException("Refresh token reuse detected. Session revoked.");
        }

        // 🔁 STEP 2: ROTATE TOKEN
        existingToken.revoke();

        UserAccount user = existingToken.getUser();

        List<String> roles = user.getRoles().stream().map(Role::getName).toList();

        List<String> permissions = user.getRoles().stream().flatMap(r -> r.getPermissions().stream()).map(Permission::getName).distinct().toList();

        String newAccessToken = jwtTokenProvider.generateToken(user.getId().toString(), user.getEmail(), roles, permissions);

        RefreshToken newRefreshToken = refreshTokenService.create(user, existingToken.getDeviceName(), existingToken.getIpAddress(), existingToken.getUserAgent());

        // 🔗 Link rotation chain
        existingToken.setReplacedBy(newRefreshToken.getId());

        refreshTokenRepository.save(existingToken);

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

        UserAccount user = repository.findById(UUID.fromString(userId)).orElseThrow(() -> new InvalidCredentialsException());

        return refreshTokenRepository.findByUser(user).stream().map(token -> new SessionResponse(token.getDeviceName(), token.getIpAddress(), token.getUserAgent(), token.isRevoked())).toList();
    }

}
