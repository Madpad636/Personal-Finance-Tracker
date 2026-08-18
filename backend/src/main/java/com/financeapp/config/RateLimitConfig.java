package com.financeapp.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Simple in-memory token-bucket rate limiter, keyed per client IP.
 * Suitable for a single-instance deployment; swap for a Redis-backed
 * bucket store if/when the app is horizontally scaled.
 */
@Component
public class RateLimitConfig {

    private final ConcurrentMap<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Bucket> apiBuckets = new ConcurrentHashMap<>();

    @Value("${app.rate-limit.login-requests-per-minute}")
    private int loginRequestsPerMinute;

    @Value("${app.rate-limit.api-requests-per-minute}")
    private int apiRequestsPerMinute;

    public Bucket resolveLoginBucket(String clientKey) {
        return loginBuckets.computeIfAbsent(clientKey, k -> newBucket(loginRequestsPerMinute));
    }

    public Bucket resolveApiBucket(String clientKey) {
        return apiBuckets.computeIfAbsent(clientKey, k -> newBucket(apiRequestsPerMinute));
    }

    private Bucket newBucket(int requestsPerMinute) {
        Bandwidth limit = Bandwidth.classic(requestsPerMinute,
                Refill.greedy(requestsPerMinute, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }
}
