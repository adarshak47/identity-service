package com.adarsh.identity_service.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.
    UsernamePasswordAuthenticationToken;

import org.springframework.security.core.authority.
    AuthorityUtils;

import org.springframework.security.core.context.
    SecurityContextHolder;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.
    OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if(header != null && header.startsWith("Bearer ")){
            String token = header.substring(7);

            if(tokenProvider.validateToken(token)){

                String userId= tokenProvider.extractUserId(token);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, AuthorityUtils.NO_AUTHORITIES);

                SecurityContextHolder
                    .getContext()
                    .setAuthentication(
                        authentication
                    );
            }
        }

        filterChain.doFilter(
            request,
            response
        );
    }
}
