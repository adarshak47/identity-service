package com.adarsh.identity_service.security.oauth;

import com.adarsh.identity_service.auth.domain.*;
import com.adarsh.identity_service.auth.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.user.*;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserAccountRepository userRepository;
    private final OAuthIdentityRepository oauthIdentityRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {

        OAuth2User oauthUser = super.loadUser(request);
        Map<String, Object> attributes = oauthUser.getAttributes();

        String email = (String) attributes.get("email");
        String providerId = (String) attributes.get("sub"); // Google unique ID

        OAuthProvider provider = OAuthProvider.GOOGLE;

        // 1. Check if OAuth identity already exists
        var existingIdentity = oauthIdentityRepository.findByProviderAndProviderUserId(provider, providerId);

        if (existingIdentity.isPresent()) {
            return oauthUser; // already linked
        }

        // 2. Find existing user by email (for linking)
        UserAccount user = userRepository.findByEmail(email).orElseGet(() -> createNewUser(email));

        // 3. Create OAuth link
        OAuthIdentity identity = new OAuthIdentity();
        identity.setId(UUID.randomUUID());
        identity.setProvider(provider);
        identity.setProviderUserId(providerId);
        identity.setEmail(email);
        identity.setUser(user);

        oauthIdentityRepository.save(identity);

        return oauthUser;
    }

    private UserAccount createNewUser(String email) {
        UserAccount user = new UserAccount(UUID.randomUUID(), email, "OAUTH2_USER", UserStatus.ACTIVE);
        return userRepository.save(user);
    }
}
