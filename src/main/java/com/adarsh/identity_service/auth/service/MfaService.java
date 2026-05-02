package com.adarsh.identity_service.auth.service;

import com.adarsh.identity_service.auth.domain.*;
import com.adarsh.identity_service.auth.repository.MfaOtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MfaService {

    private final MfaOtpRepository repository;
    private final JavaMailSender mailSender;

    public String generateOtp(UserAccount user) {

        String otp = String.valueOf(100000 + new SecureRandom().nextInt(900000));

        MfaOtp entity = new MfaOtp();
        entity.setId(UUID.randomUUID());
        entity.setUser(user);
        entity.setOtpHash(hash(otp));
        entity.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        repository.save(entity);

        sendEmail(user.getEmail(), otp);

        return otp;
    }

    public boolean verifyOtp(UserAccount user, String otp) {

        MfaOtp stored = repository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        return stored.getOtpHash().equals(hash(otp));
    }

    private String hash(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes());
    }

    private void sendEmail(String email, String otp) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("Your MFA OTP");
        msg.setText("Your OTP is: " + otp + " (valid 5 minutes)");

        mailSender.send(msg);
    }
}
