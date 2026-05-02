package com.adarsh.identity_service.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    private final JwtBlacklistService blacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            if (tokenProvider.validateToken(token)) {

                Claims claims = Jwts.parserBuilder().setSigningKey(tokenProvider.getSecretKey()).build().parseClaimsJws(token).getBody();

                String jti = claims.getId(); // 🔥 NEW

                // 🚨 BLOCK if blacklisted
                if (blacklistService.isBlacklisted(jti)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token revoked");
                    return;
                }

                String userId = claims.getSubject();

                List<String> roles = claims.get("roles", List.class);
                List<String> permissions = claims.get("permissions", List.class);

                List<SimpleGrantedAuthority> authorities = new ArrayList<>();

                if (roles != null) {
                    roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
                }

                if (permissions != null) {
                    permissions.forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission)));
                }

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
