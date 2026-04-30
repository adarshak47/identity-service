package com.adarsh.identity_service.auth.repository;

import com.adarsh.identity_service.auth.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    Optional<Permission> findByName(String name);
}
