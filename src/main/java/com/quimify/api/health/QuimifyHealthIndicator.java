package com.quimify.api.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

// Spring boot Health Indicator for Quimify API (native + custom health check)

@Primary
@Component
class QuimifyHealthIndicator implements HealthIndicator {

    private final HealthService healthService;

    public QuimifyHealthIndicator(HealthService healthService) {
        this.healthService = healthService;
    }

    @Override
    public Health health() {
        HealthResult healthResult = healthService.health();

        if (healthResult.isHealthy()) {
            return new Health.Builder()
                    .up()
                    .withDetail("message", healthResult.getMessage())
                    .build();
        } else {
            return new Health.Builder()
                    .down()
                    .withDetail("error", healthResult.getMessage())
                    .build();
        }
    }
}
