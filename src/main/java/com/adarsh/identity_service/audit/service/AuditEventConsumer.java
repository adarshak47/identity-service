package com.adarsh.identity_service.audit.service;

import com.adarsh.identity_service.audit.domain.AuditLog;
import com.adarsh.identity_service.audit.event.AuditEvent;
import com.adarsh.identity_service.audit.queue.AuditEventQueue;
import com.adarsh.identity_service.audit.repository.AuditLogRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditEventConsumer {

    private final AuditEventQueue queue;
    private final AuditLogRepository repository;

    @PostConstruct
    public void startConsumer() {

        log.info("🚀 AuditEventConsumer started");

        Thread worker = new Thread(() -> {

            while (true) {
                try {
                    log.debug("Waiting for audit event...");

                    AuditEvent event = queue.consume();

                    log.info("Processing audit event: {}", event.action());

                    AuditLog logEntity = new AuditLog(
                        event.action(),
                        event.actor(),
                        event.details(),
                        event.remoteIp()
                    );

                    repository.save(logEntity);

                    log.info("Audit event saved to DB");

                } catch (Exception e) {
                    log.error("Audit consumer failed", e);
                }
            }
        });

        worker.setName("audit-consumer-thread");
        worker.setDaemon(true);
        worker.start();
    }
}
