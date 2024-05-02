package com.quimify.api.balancer;

public class BalancerResult {
    private boolean present;
    private String originalEquation;
    private String balancedEquation;

    private String balancedReactants;


    private String balancedProducts;
    private String error;

    BalancerResult(boolean present, String originalEquation, String balancedEquation, String balancedReactants, String balancedProducts) {
        this.present = present;
        this.originalEquation = originalEquation;
        this.balancedEquation = balancedEquation;
        this.balancedReactants = balancedReactants;
        this.balancedProducts = balancedProducts;
    }

    static BalancerResult error(String error) {
        BalancerResult balancerResult = new BalancerResult(false, null, null, null, null);
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