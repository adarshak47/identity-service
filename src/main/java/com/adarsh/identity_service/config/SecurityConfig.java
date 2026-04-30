package com.adarsh.identity_service.config;

import com.adarsh.identity_service.security.jwt.JwtAuthenticationFilter;
import com.adarsh.identity_service.security.oauth.CustomOAuth2UserService;
import com.adarsh.identity_service.security.oauth.OAuth2SuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(
                    (request, response, e) ->
                        response.sendError(
                            HttpServletResponse.SC_UNAUTHORIZED,
                            "Unauthorized"
                        )
                )
            )

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/v1/auth/**",
                    "/oauth2/**",
                    "/login/**"
                ).permitAll()

                .anyRequest().authenticated()
            )

            // 🔥 OAuth2 CONFIG (FULL)
            .oauth2Login(oauth -> oauth
                .userInfoEndpoint(user ->
                    user.userService(customOAuth2UserService)
                )
                .successHandler(oAuth2SuccessHandler)
            )

            .addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
            )

            .build();
    }
}
