package com.quimify.api.health;

public class HealthResult {

    private final boolean present;
    private final String message;

    public HealthResult(boolean present, String message) {
        this.present = present;
        this.message = message;
    }

    public static HealthResult healthy(String message) {
        return new HealthResult(true, message);
    }

    public static HealthResult error(String message) {
        return new HealthResult(false, message);
    }

    public boolean isPresent() {
        return present;
    }

    public String getMessage() {
        return message;
    }
}
