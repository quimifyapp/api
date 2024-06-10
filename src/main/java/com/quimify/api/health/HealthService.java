package com.quimify.api.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

// Service where all logic for health checks are implemented

@Service
public class HealthService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    // TODO: Implement dependencies for health checks.

    public HealthResult checkOverallHealth() {
        try {
            // EN PROCESO, PARA EL PROXIMO COMMIT SE TERMINA
            return new HealthResult(true, "All systems operational"); 

        } catch (Exception e) {
            logger.error("Health check failed", e);
            return new HealthResult(false, e.getMessage());
        }
    }
}
