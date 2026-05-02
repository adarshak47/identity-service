package com.adarsh.identity_service.user.service;

import com.adarsh.identity_service.auth.repository.RefreshTokenRepository;
import com.adarsh.identity_service.user.dto.DeviceSessionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final RefreshTokenRepository refreshTokenRepository;

    public List<DeviceSessionResponse> getUserSessions(UUID userId) {

        return refreshTokenRepository
            .findAllByUser_IdOrderByCreatedAtDesc(userId)
            .stream()
            .map(token -> new DeviceSessionResponse(
                token.getId().toString(),
                token.getDeviceName(),
                token.getIpAddress(),
                token.getUserAgent(),
                token.isRevoked(),
                token.getCreatedAt()
            ))
            .toList();
    }

    public void revokeSession(UUID sessionId, UUID userId) {

        var token = refreshTokenRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!token.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    public void revokeAllOtherSessions(UUID userId, UUID currentSessionId) {

        var sessions = refreshTokenRepository.findAllByUser_IdAndRevokedFalse(userId);

        sessions.forEach(session -> {
            if (!session.getId().equals(currentSessionId)) {
                session.setRevoked(true);
            }
        });

        refreshTokenRepository.saveAll(sessions);
    }
}
