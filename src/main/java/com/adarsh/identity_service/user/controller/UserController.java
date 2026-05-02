package com.adarsh.identity_service.user.controller;

import com.adarsh.identity_service.auth.service.AuthenticationService;
import com.adarsh.identity_service.user.dto.DeviceSessionResponse;
import com.adarsh.identity_service.user.dto.SessionResponse;
import com.adarsh.identity_service.common.response.ApiResponse;
import com.adarsh.identity_service.user.dto.AdminResponse;
import com.adarsh.identity_service.user.dto.UserMeResponse;
import com.adarsh.identity_service.user.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationService authenticationService;
    private final DeviceService deviceService;

    @GetMapping("/me")
    public ApiResponse<UserMeResponse> me(Authentication authentication) {
        return ApiResponse.success(new UserMeResponse(authentication.getName()));
    }

    @PreAuthorize("hasAuthority('READ_USER')")
    @GetMapping("/admin")
    public ApiResponse<AdminResponse> adminOnly() {
        return ApiResponse.success(new AdminResponse("Admin access granted"));
    }

    @GetMapping("/sessions")
    public ApiResponse<List<DeviceSessionResponse>> sessions(Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());

        return ApiResponse.success(deviceService.getUserSessions(userId));
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ApiResponse<Void> revokeSession(@PathVariable UUID sessionId, Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());

        deviceService.revokeSession(sessionId, userId);

        return ApiResponse.success(null);
    }

    @PostMapping("/sessions/revoke-others")
    public ApiResponse<Void> revokeOthers(@RequestParam UUID currentSessionId, Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());

        deviceService.revokeAllOtherSessions(userId, currentSessionId);

        return ApiResponse.success(null);
    }
}
