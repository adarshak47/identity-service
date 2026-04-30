package com.adarsh.identity_service.user.controller;

import com.adarsh.identity_service.auth.service.AuthenticationService;
import com.adarsh.identity_service.user.dto.SessionResponse;
import com.adarsh.identity_service.common.response.ApiResponse;
import com.adarsh.identity_service.user.dto.AdminResponse;
import com.adarsh.identity_service.user.dto.UserMeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationService authenticationService;

    @GetMapping("/me")
    public ApiResponse<UserMeResponse> me(Authentication authentication) {
        return ApiResponse.success(
            new UserMeResponse(authentication.getName())
        );
    }

    @PreAuthorize("hasAuthority('READ_USER')")
    @GetMapping("/admin")
    public ApiResponse<AdminResponse> adminOnly() {
        return ApiResponse.success(new AdminResponse("Admin access granted"));
    }

    @GetMapping("/sessions")
    public ApiResponse<List<SessionResponse>> sessions(Authentication authentication) {
        return ApiResponse.success(
            authenticationService.getUserSessions(authentication.getName())
        );
    }
}
