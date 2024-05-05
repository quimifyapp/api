package com.quimify.api.balancer;

public class BalancerResult {
    private boolean present;
    private String originalEquation;
    private String originalReactants;
    private String originalProducts;
    private String balancedEquation;
    private String balancedReactants;
    private String balancedProducts;
    private String error;

    BalancerResult(boolean present, String originalEquation, String originalReactants, String originalProducts,
                   String balancedEquation, String balancedReactants, String balancedProducts) {
        this.present = present;
        this.originalEquation = originalEquation;
        this.originalReactants = originalReactants;
        this.originalProducts = originalProducts;
        this.balancedEquation = balancedEquation;
        this.balancedReactants = balancedReactants;
        this.balancedProducts = balancedProducts;
    }

    static BalancerResult error(String error) {
        BalancerResult balancerResult = new BalancerResult(false, null, null, null, null, null, null);
        balancerResult.error = error;
        return balancerResult;
    }

    public String getOriginalEquation() {
        return originalEquation;
    }

    public void setOriginalEquation(String originalEquation) {
        this.originalEquation = originalEquation;
    }

    public void setBalancedEquation(String balancedEquation) {
        this.balancedEquation = balancedEquation;
    }

    public String getOriginalReactants() {
        return originalReactants;
    }

    public void setOriginalReactants(String originalReactants) {
        this.originalReactants = originalReactants;
    }

    public String getOriginalProducts() {
        return originalProducts;
    }

    public void setOriginalProducts(String originalProducts) {
        this.originalProducts = originalProducts;
    }

    public String getBalancedEquation() {
        return balancedEquation;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
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
}