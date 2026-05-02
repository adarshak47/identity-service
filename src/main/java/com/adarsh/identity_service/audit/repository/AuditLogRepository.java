package com.adarsh.identity_service.audit.repository;

import com.adarsh.identity_service.audit.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {}
