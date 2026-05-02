package com.adarsh.identity_service.audit.service;

import com.adarsh.identity_service.audit.domain.AuditLog;
import com.adarsh.identity_service.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    private final AuditLogRepository repo;

    public void log(String action, String actor, String details, String remoteIp) {
        repo.save(new AuditLog(action, actor, details, remoteIp));
    }
}
