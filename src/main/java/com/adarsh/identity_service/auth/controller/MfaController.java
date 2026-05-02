package com.adarsh.identity_service.auth.controller;

import com.adarsh.identity_service.auth.domain.MfaRequest;
import com.adarsh.identity_service.auth.dto.LoginResponse;
import com.adarsh.identity_service.auth.service.AuthenticationService;
import com.adarsh.identity_service.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/mfa")
@RequiredArgsConstructor
public class MfaController {

    private final AuthenticationService authService;

    @PostMapping("/verify")
    public ApiResponse<LoginResponse> verify(@RequestBody MfaRequest request) {
        return ApiResponse.success(authService.verifyMfa(request));
    }
}
