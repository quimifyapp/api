package com.quimify.api.health;

import java.util.List;

class HealthResult {

    private boolean healthy;
    private List<String> errors;

    HealthResult(boolean healthy, List<String> errors) {
        this.healthy = healthy;
        this.errors = errors;
    }

    // Getters and setters (must be defined and public to enable JSON serialization):

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

}
