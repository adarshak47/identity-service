package com.adarsh.identity_service.auth.controller;

import com.adarsh.identity_service.auth.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class EmailVerificationController {
    private final EmailVerificationService emailVerificationService;

    @GetMapping("/verify-email")
    public ResponseEntity<Map<String, Object>> verifyEmail(@RequestParam String token) {
        try {
            emailVerificationService.verify(token);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Email verified! You may now log in."
            ));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", ex.getMessage()
            ));
        }
    }
}
