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
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http.csrf(csrf -> csrf.disable())

            .cors(cors -> cors.configurationSource(corsConfigurationSource))

            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, e) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")))

            .authorizeHttpRequests(auth -> auth

                // CORS preflight
                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                // Public endpoints
                .requestMatchers("/api/v1/auth/**", "/oauth2/**", "/login/**", "/actuator/health").permitAll()

                // 🔐 Actuator protected
                .requestMatchers("/actuator/**").hasRole("ADMIN")

                .requestMatchers("/actuator/prometheus").hasRole("ADMIN")

                .anyRequest().authenticated())


            .oauth2Login(oauth -> oauth.userInfoEndpoint(user -> user.userService(customOAuth2UserService)).successHandler(oAuth2SuccessHandler))

            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

            .build();
    }
}
