package com.quimify.api.health;

import java.util.ArrayList;
import java.util.List;

class HealthResult {
    private boolean healthy;
    private List<String> errors;

    // Constructor defaults to healthy with no errors
    HealthResult() {
        this.healthy = true;
        this.errors = new ArrayList<>();
    }

    // Methods to add errors and modify health status
    void addError(String error) {
        this.errors.add(error);
        this.healthy = false; // Any error makes the system unhealthy
    }

    // Getters
    boolean isHealthy() {
        return healthy;
    }

    List<String> getErrors() {
        return errors;
    }
}
