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
import com.adarsh.identity_service.metrics.AuthMetrics;
import com.adarsh.identity_service.security.jwt.JwtBlacklistService;
import com.adarsh.identity_service.security.jwt.JwtTokenProvider;
import com.adarsh.identity_service.security.ratelimit.RateLimiterService;
import com.adarsh.identity_service.user.dto.SessionResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.micrometer.tracing.Tracer;
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
    private final JwtBlacklistService jwtBlacklistService;
    private final AuthMetrics authMetrics;
    private final Tracer tracer;
    private final MfaService mfaService;

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
        authMetrics.incrementRegistration();
        auditLogService.log("REGISTER", user.getEmail(), "User registered", ip);
        return new RegisterResponse(savedUser.getId(), savedUser.getEmail(), "User registered successfully");
    }

    public LoginResponse login(LoginRequest request) {

        var span = tracer.nextSpan().name("auth.login").start();

        try (Tracer.SpanInScope ws = tracer.withSpan(span)) {

            String ip = requestContext.getClientIp();

            try {
                String email = InputNormalizer.normalizeEmail(request.email());

                String rateLimitKey = requestContext.getClientIp() + ":" + email;

                if (!rateLimiterService.isAllowed(rateLimitKey)) {
                    span.tag("login.status", "rate_limited");
                    throw new RateLimitExceededException();
                }

                UserAccount user = repository.findByEmail(email).orElseThrow(InvalidCredentialsException::new);

                if (user.isAccountLocked()) {
                    span.tag("login.status", "locked");
                    throw new AccountLockedException();
                }

                if (!user.isActive()) {
                    span.tag("login.status", "inactive");
                    throw new UserNotActiveException(user.getEmail());
                }

                if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {

                    user.incrementFailedAttempts(2, 1);
                    repository.save(user);

                    span.tag("login.status", "bad_password");

                    throw new InvalidCredentialsException();
                }

                // ✅ SUCCESS BLOCK
                user.resetFailedAttempts();
                repository.save(user);

                rateLimiterService.reset(rateLimitKey);

                span.tag("login.status", "success");
                span.tag("user.email", email);

                // CHECK MFA
                if (user.isMfaEnabled()) {

                    String otp = mfaService.generateOtp(user);

                    return new LoginResponse(null, null, "MFA_REQUIRED");
                } else {
                    List<String> roles = user.getRoles().stream().map(Role::getName).toList();

                    List<String> permissions = user.getRoles().stream().flatMap(r -> r.getPermissions().stream()).map(Permission::getName).distinct().toList();

                    String accessToken = jwtTokenProvider.generateToken(user.getId().toString(), user.getEmail(), roles, permissions);

                    RefreshToken refreshToken = refreshTokenService.create(user, request.deviceName(), requestContext.getClientIp(), requestContext.getUserAgent());

                    return new LoginResponse(accessToken, refreshToken.getRawToken(), "Bearer");
                }

            } catch (Exception e) {

                span.tag("login.status", "failed");
                span.tag("error.type", e.getClass().getSimpleName());

                throw e;
            }

        } finally {
            span.end();   // 🔥 VERY IMPORTANT
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

            throw new RefreshTokenReuseException();
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

    public void logout(LogoutRequest request, String accessToken) {

        // 🔥 Extract JTI
        Claims claims = Jwts.parserBuilder().setSigningKey(jwtTokenProvider.getSecretKey()).build().parseClaimsJws(accessToken).getBody();

        String jti = claims.getId();
        long expiry = claims.getExpiration().getTime();

        // 🔥 Blacklist access token
        jwtBlacklistService.blacklist(jti, expiry);

        // 🔁 Existing refresh token revoke
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

    public LoginResponse verifyMfa(MfaRequest request) {

        String email = InputNormalizer.normalizeEmail(request.email());

        UserAccount user = repository.findByEmail(email).orElseThrow(InvalidCredentialsException::new);

        // 1. Check OTP
        boolean valid = mfaService.verifyOtp(user, request.otp());

        if (!valid) {
            auditLogService.log("MFA_FAIL", email, "Invalid MFA OTP", requestContext.getClientIp());
            throw new InvalidCredentialsException();
        }

        // 2. OTP success → generate tokens
        List<String> roles = user.getRoles().stream().map(Role::getName).toList();

        List<String> permissions = user.getRoles().stream().flatMap(r -> r.getPermissions().stream()).map(Permission::getName).distinct().toList();

        String accessToken = jwtTokenProvider.generateToken(user.getId().toString(), user.getEmail(), roles, permissions);

        RefreshToken refreshToken = refreshTokenService.create(user, "MFA_LOGIN", requestContext.getClientIp(), requestContext.getUserAgent());

        auditLogService.log("MFA_SUCCESS", email, "MFA verification successful", requestContext.getClientIp());

        return new LoginResponse(accessToken, refreshToken.getRawToken(), "Bearer");
    }

}
