package com.adarsh.identity_service.security.jwt;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtBlacklistService {

    private static class BlacklistedToken {
        long expiry;
    }

    private final Map<String, BlacklistedToken> blacklist = new ConcurrentHashMap<>();

    public void blacklist(String jti, long expiryTimeMillis) {
        BlacklistedToken token = new BlacklistedToken();
        token.expiry = expiryTimeMillis;
        blacklist.put(jti, token);
    }

    public boolean isBlacklisted(String jti) {
        BlacklistedToken token = blacklist.get(jti);

        if (token == null) return false;

        // Cleanup expired
        if (Instant.now().toEpochMilli() > token.expiry) {
            blacklist.remove(jti);
            return false;
        }

        return true;
    }
}
