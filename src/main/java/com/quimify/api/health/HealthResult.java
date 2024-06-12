package com.quimify.api.health;

import java.util.ArrayList;
import java.util.List;

public class HealthResult {
    private boolean present;
    private String message;
    private List<String> errors;

    public HealthResult(boolean present, String message) {
        this.present = present;
        this.message = message;
        this.errors = new ArrayList<>();
    }

    public HealthResult(boolean present, String message, String error) {
        this(present, message);
        this.errors.add(error);
    }

    public void addError(String error) {
        this.errors.add(error);
    }

    // Getters
    public boolean isPresent() {
        return present && errors.isEmpty(); // Check both conditions Just in case
    }

    public boolean setPresent(boolean present) {
        this.present = present;
        return this.present;
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
