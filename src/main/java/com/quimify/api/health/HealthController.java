package com.quimify.api.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    @Autowired
    HealthIndicator healthIndicator;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public HealthController(QuimifyHealthIndicator healthIndicator) {
        this.healthIndicator = healthIndicator;
    }

    @GetMapping
    public Health getHealth() {
        logger.info(String.format(healthIndicator.health().toString()));
        return healthIndicator.health();
    }
}
