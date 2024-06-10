package com.quimify.api.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    private final QuimifyHealthIndicator healthIndicator;

    public HealthController(QuimifyHealthIndicator healthIndicator) {
        this.healthIndicator = healthIndicator;
    }

    @GetMapping
    public Health getHealth() {
        return healthIndicator.health();
    }
}
