package com.quimify.api.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
class HealthController {

    @Autowired
    HealthService healthService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    // Client:

    @GetMapping
    public HealthResult getHealth() {
        HealthResult healthResult = healthService.checkHealth();

        if(healthResult.isHealthy())
            logger.info("GET health: OK");

        return healthResult;
    }
}
