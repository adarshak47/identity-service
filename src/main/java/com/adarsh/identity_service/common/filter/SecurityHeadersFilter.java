package com.adarsh.identity_service.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SecurityHeadersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletResponse res = (HttpServletResponse) response;

        // 🔒 Prevent clickjacking
        res.setHeader("X-Frame-Options", "DENY");

        // 🔒 Prevent MIME sniffing
        res.setHeader("X-Content-Type-Options", "nosniff");

        // 🔒 XSS protection (legacy but still useful)
        res.setHeader("X-XSS-Protection", "1; mode=block");

        // 🔒 Strict Transport Security (HTTPS only)
        res.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

        // 🔒 Content Security Policy (VERY IMPORTANT)
        res.setHeader("Content-Security-Policy", "default-src 'self'; script-src 'self'; object-src 'none';");

        chain.doFilter(request, response);
    }
}
