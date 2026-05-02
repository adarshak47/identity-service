package com.adarsh.identity_service.auth.repository;

import com.adarsh.identity_service.auth.domain.OAuthIdentity;
import com.adarsh.identity_service.auth.domain.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OAuthIdentityRepository extends JpaRepository<OAuthIdentity, UUID> {

    Optional<OAuthIdentity> findByProviderAndProviderUserId(OAuthProvider provider, String providerUserId);
}
