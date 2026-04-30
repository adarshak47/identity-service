package com.adarsh.identity_service.security.oauth;

import com.adarsh.identity_service.auth.domain.UserAccount;
import com.adarsh.identity_service.auth.domain.UserStatus;
import com.adarsh.identity_service.auth.repository.UserAccountRepository;
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

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {

        OAuth2User oauthUser = super.loadUser(request);

        Map<String, Object> attributes = oauthUser.getAttributes();

        String email = (String) attributes.get("email");

        UserAccount user = userRepository.findByEmail(email)
            .orElseGet(() -> registerNewUser(email));

        return oauthUser;
    }

    private UserAccount registerNewUser(String email) {
        UserAccount user = new UserAccount(
            UUID.randomUUID(),
            email,
            "OAUTH2_USER",
            UserStatus.ACTIVE
        );

        return userRepository.save(user);
    }
}
