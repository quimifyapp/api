package com.quimify.api.health;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    @Autowired
    private HealthService healthService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping
    public Map<String, Object> getHealth() {
        Map<String, Object> healthResult = healthService.health();
        logger.info("Health Check: {}", healthResult);
        return healthResult;
    }
}
