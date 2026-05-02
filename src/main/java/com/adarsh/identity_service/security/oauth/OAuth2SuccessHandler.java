package com.adarsh.identity_service.security.oauth;

import com.adarsh.identity_service.auth.domain.UserAccount;
import com.adarsh.identity_service.auth.repository.UserAccountRepository;
import com.adarsh.identity_service.auth.service.RefreshTokenService;
import com.adarsh.identity_service.security.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserAccountRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
        throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");

        UserAccount user = userRepository.findByEmail(email)
            .orElseThrow();

        List<String> roles = user.getRoles().stream()
            .map(r -> r.getName())
            .toList();

        List<String> permissions = user.getRoles()
            .stream()
            .flatMap(r -> r.getPermissions().stream())
            .map(p -> p.getName())
            .distinct()
            .toList();

        String accessToken = jwtTokenProvider.generateToken(
            user.getId().toString(),
            user.getEmail(),
            roles,
            permissions
        );

        var refreshToken = refreshTokenService.create(
            user,
            "GOOGLE_OAUTH",
            request.getRemoteAddr(),
            request.getHeader("User-Agent")
        );

        response.setContentType("application/json");

        response.getWriter().write(
            """
            {
              "accessToken": "%s",
              "refreshToken": "%s",
              "tokenType": "Bearer"
            }
            """.formatted(accessToken, refreshToken.getRawToken())
        );
    }
}
