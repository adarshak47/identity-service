package com.adarsh.identity_service.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;

    private final long expiration;

    public JwtTokenProvider(
        @Value("${security.jwt.secret}")
        String secret,

        @Value("${security.jwt.access-token-expiration}")
        long expiration
    ) {
        this.secretKey= Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration=expiration;
    }

    public String generateToken(
        String userId,
        String email,
        List<String> roles
    ){
        Date now = new Date();
        Date expiry = new Date(
            now.getTime() + expiration
        );

        return Jwts.builder()
            .setSubject(userId)
            .claim("email", email)
            .claim("roles", roles)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(secretKey)
            .compact();
    }

    public String extractUserId(String token){
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch(Exception e){
            return false;
        }
    }

    public SecretKey getSecretKey(){
        return this.secretKey;
    }
}
