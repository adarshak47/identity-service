package com.adarsh.identity_service.auth.service;

import com.adarsh.identity_service.audit.service.AuditLogService;
import com.adarsh.identity_service.auth.domain.UserAccount;
import com.adarsh.identity_service.auth.domain.EmailVerificationToken;
import com.adarsh.identity_service.auth.exception.InvalidVerificationTokenException;
import com.adarsh.identity_service.auth.repository.EmailVerificationTokenRepository;
import com.adarsh.identity_service.auth.repository.UserAccountRepository;
import com.adarsh.identity_service.common.web.RequestContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private final EmailVerificationTokenRepository tokenRepository;
    private final UserAccountRepository userRepository;
    private final JavaMailSender mailSender;
    private final AuditLogService auditLogService;
    private final RequestContext requestContext;

    @Value("${app.backend-base-url}")
    private String backendBaseUrl;

    @Value("${app.verification.token-validity-minutes:60}")
    private int tokenValidityMins;

    public void sendVerificationEmail(UserAccount user) {
        // Remove previous tokens for this user
        tokenRepository.deleteAllByUser_Id(user.getId());

        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = new EmailVerificationToken(
            user,
            token,
            LocalDateTime.now().plusMinutes(tokenValidityMins)
        );
        tokenRepository.save(verificationToken);
        String link = backendBaseUrl + "/api/v1/auth/verify-email?token=" + token;

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(user.getEmail());
        mail.setSubject("Verify your email address");
        mail.setText("Please verify your email by visiting:\n" + link + "\n\nThis link will expire in " + tokenValidityMins + " minutes.");

        mailSender.send(mail);
    }

    @Transactional
    public void verify(String token) {
        String ip = requestContext.getClientIp();
        EmailVerificationToken ver = tokenRepository.findByToken(token)
            .orElseThrow(() -> new InvalidVerificationTokenException("Invalid verification token."));
        if (ver.isExpired()) throw new InvalidVerificationTokenException("Invalid verification token.");
        if (ver.isVerified()) throw new InvalidVerificationTokenException("Invalid verification token.");
        ver.markVerified();
        ver.getUser().setStatusToActive();
        tokenRepository.save(ver);
        userRepository.save(ver.getUser());
        auditLogService.log("EMAIL_VERIFY", ver.getUser().getEmail(), "Email verified", ip);
    }
}
