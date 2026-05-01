package com.adarsh.identity_service.auth.service;

import com.adarsh.identity_service.auth.domain.UserAccount;
import com.adarsh.identity_service.auth.domain.EmailVerificationToken;
import com.adarsh.identity_service.auth.repository.EmailVerificationTokenRepository;
import com.adarsh.identity_service.auth.repository.UserAccountRepository;
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
        EmailVerificationToken ver = tokenRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException("Invalid verification token."));
        if (ver.isExpired()) throw new RuntimeException("Token expired.");
        if (ver.isVerified()) throw new RuntimeException("Already verified.");
        ver.markVerified();
        ver.getUser().setStatusToActive();
        tokenRepository.save(ver);
        userRepository.save(ver.getUser());
    }
}
