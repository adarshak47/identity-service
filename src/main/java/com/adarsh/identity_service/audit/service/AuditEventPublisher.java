package com.adarsh.identity_service.audit.service;

import com.adarsh.identity_service.audit.event.AuditEvent;
import com.adarsh.identity_service.audit.queue.AuditEventQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditEventPublisher {

    private final AuditEventQueue queue;

    public void publish(String action, String actor, String details, String ip) {

        log.info("Publishing audit event: {}", action);

        AuditEvent event = new AuditEvent(
            action, actor, details, ip, LocalDateTime.now()
        );

        queue.publish(event);
    }
}
