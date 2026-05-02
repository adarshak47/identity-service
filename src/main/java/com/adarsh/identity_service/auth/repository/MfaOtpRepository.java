package com.adarsh.identity_service.auth.repository;

import com.adarsh.identity_service.auth.domain.MfaOtp;
import com.adarsh.identity_service.auth.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MfaOtpRepository extends JpaRepository<MfaOtp, UUID> {

    Optional<MfaOtp> findByUser(UserAccount user);
}
