package com.adarsh.identity_service.security.ratelimit;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    private static class Attempt {
        int count;
        long windowStart;
    }

    private final Map<String, Attempt> cache = new ConcurrentHashMap<>();

    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_MS = 60_000; // 1 minute

    public synchronized boolean isAllowed(String key) {

        long now = Instant.now().toEpochMilli();

        Attempt attempt = cache.get(key);

        if (attempt == null) {
            attempt = new Attempt();
            attempt.windowStart = now;
            attempt.count = 1;
            cache.put(key, attempt);
            return true;
        }

        // reset window
        if (now - attempt.windowStart > WINDOW_MS) {
            attempt.windowStart = now;
            attempt.count = 1;
            cache.put(key, attempt);
            return true;
        }

        attempt.count++;

        if (attempt.count > MAX_ATTEMPTS) {
            cache.put(key, attempt);
            return false;
        }

        cache.put(key, attempt);
        return true;
    }

    public void reset(String key) {
        cache.remove(key);
    }
}
