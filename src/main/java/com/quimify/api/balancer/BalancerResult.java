package com.quimify.api.balancer;

// This POJO class represents responses of balanced equations to the client.

class BalancerResult {
    private boolean present;

    private String balancedReactants;
    private String balancedProducts;

    private String error;

    BalancerResult(String balancedReactants, String balancedProducts) {
        this.present = true;
        this.balancedReactants = balancedReactants;
        this.balancedProducts = balancedProducts;
    }

    private BalancerResult(String error) {
        this.present = false;
        this.error = error;
    }

    static BalancerResult error(String error) {
        return new BalancerResult(error);
    }

    static BalancerResult notPresent() {
        return new BalancerResult(null);
    }

    // Getters and setters (must be public to enable JSON serialization):

    // TODO remove
    String getBalancedEquation() {
        return balancedReactants + " = " + balancedProducts;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public String getBalancedReactants() {
        return balancedReactants;
    }

    public void setBalancedReactants(String balancedReactants) {
        this.balancedReactants = balancedReactants;
    }

    public String getBalancedProducts() {
        return balancedProducts;
    }

    public void setBalancedProducts(String balancedProducts) {
        this.balancedProducts = balancedProducts;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}