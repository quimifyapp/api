package com.quimify.api.health;

import java.util.ArrayList;
import java.util.List;

public class HealthResult {
    private boolean healthy;
    private String message;
    private List<String> errors;

    public HealthResult(boolean healthy, String message) {
        this.healthy = healthy;
        this.message = message;
        this.errors = new ArrayList<>();
    }

    public HealthResult(boolean healthy, String message, String error) {
        this(healthy, message);
        this.errors.add(error);
    }

    public void addError(String error) {
        this.errors.add(error);
    }

    // Getters
    public boolean isHealthy() {
        return healthy && errors.isEmpty(); // Check both conditions Just in case
    }

    public boolean setHealthy(boolean healthy) {
        this.healthy = healthy;
        return this.healthy;
    }

    public String setMessage(String message) {
        this.message = message;
        return this.message;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getErrors() {
        return errors;
    }
}
