package com.quimify.api.equation;

// This POJO class represents responses of balanced equations to the client.

class EquationResult {
    private boolean present;

    private String balancedReactants;
    private String balancedProducts;

    private String error;

    EquationResult(String balancedReactants, String balancedProducts) {
        this.present = true;
        this.balancedReactants = balancedReactants;
        this.balancedProducts = balancedProducts;
    }

    private EquationResult(String error) {
        this.present = false;
        this.error = error;
    }

    static EquationResult error(String error) {
        return new EquationResult(error);
    }

    static EquationResult notPresent() {
        return new EquationResult(null);
    }

    // Getters and setters (must be public to enable JSON serialization):

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
