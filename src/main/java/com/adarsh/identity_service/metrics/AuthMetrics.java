package com.adarsh.identity_service.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class AuthMetrics {

    private final Counter loginSuccessCounter;
    private final Counter loginFailureCounter;
    private final Counter registrationCounter;

    public AuthMetrics(MeterRegistry registry) {
        this.loginSuccessCounter = Counter.builder("auth.login.success")
            .description("Successful login attempts")
            .register(registry);

        this.loginFailureCounter = Counter.builder("auth.login.failure")
            .description("Failed login attempts")
            .register(registry);

        this.registrationCounter = Counter.builder("auth.register.success")
            .description("Successful user registrations")
            .register(registry);
    }

    public void incrementLoginSuccess() {
        loginSuccessCounter.increment();
    }

    public void incrementLoginFailure() {
        loginFailureCounter.increment();
    }

    public void incrementRegistration() {
        registrationCounter.increment();
    }
}
