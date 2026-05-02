package com.adarsh.identity_service.common.web;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestContext {

    private final HttpServletRequest request;

    public String getClientIp() {

        String xForwardedFor = request.getHeader("X-Forwarded-For");

        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    public String getUserAgent() {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null ? userAgent : "UNKNOWN";
    }
}
