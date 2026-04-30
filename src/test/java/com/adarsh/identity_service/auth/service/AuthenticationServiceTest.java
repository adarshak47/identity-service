package com.adarsh.identity_service.auth.service;

import com.adarsh.identity_service.auth.domain.RefreshToken;
import com.adarsh.identity_service.auth.domain.UserAccount;
import com.adarsh.identity_service.auth.dto.LoginRequest;
import com.adarsh.identity_service.auth.repository.UserAccountRepository;
import com.adarsh.identity_service.security.jwt.JwtTokenProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private UserAccountRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldLoginSuccessfully() {
        UserAccount user = new UserAccount(UUID.randomUUID(), "test@mail.com", "encoded", null);

        RefreshToken mockRefreshToken = new RefreshToken(UUID.randomUUID(), "hash", user, null, false, null, null, null);
        mockRefreshToken.setRawToken("refresh-token");

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded")).thenReturn(true);
        when(jwtTokenProvider.generateToken(any(), any(), any(), any())).thenReturn("access-token");

        when(refreshTokenService.create(any(), any(), any(), any())).thenReturn(mockRefreshToken);

        var response = authenticationService.login(new LoginRequest("test@mail.com", "password", "a"));

        assertNotNull(response);
        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
    }

    @Test
    void shouldFailLoginForWrongPassword() {
        UserAccount user = new UserAccount(UUID.randomUUID(), "test@mail.com", "encoded", null);
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);
        assertThrows(RuntimeException.class, () -> authenticationService.login(new LoginRequest("test@mail.com", "wrong", "a")));
    }
}
