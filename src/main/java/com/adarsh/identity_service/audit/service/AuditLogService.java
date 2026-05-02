package com.adarsh.identity_service.audit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditEventPublisher publisher;

    public void log(String action, String actor, String details, String remoteIp) {

        publisher.publish(action, actor, details, remoteIp);
    }
}
