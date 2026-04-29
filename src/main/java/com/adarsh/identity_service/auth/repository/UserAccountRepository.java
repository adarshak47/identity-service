package com.adarsh.identity_service.auth.repository;

import com.adarsh.identity_service.auth.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {

    boolean existsByEmail(String email);

    Optional<UserAccount> findByEmail(String email);
}
