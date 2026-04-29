package com.adarsh.identity_service.auth.controller;

import com.adarsh.identity_service.auth.dto.*;
import com.adarsh.identity_service.auth.service.AuthenticationService;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(
        @Valid @RequestBody RegisterRequest request
    ) {
        return authenticationService.registerUser(request);
    }

    @PostMapping("/login")
    public LoginResponse login(
        @Valid @RequestBody LoginRequest request
    ){
        return authenticationService.login(request);
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(
        @RequestBody @Valid RefreshRequest request
    ){
        return authenticationService.refreshToken(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(
        @Valid @RequestBody LogoutRequest request
    ){
        authenticationService.logout(request);
    }
}
