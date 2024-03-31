package com.quimify.api.balancer;

public class BalancerResult {
    private boolean present;
    private String originalEquation;
    private String balancedEquation;
    private String error;

    BalancerResult(boolean present, String originalEquation, String balancedEquation){
        this.present = present;
        this.originalEquation = originalEquation;
        this.balancedEquation = balancedEquation;
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

    static BalancerResult error(String error) {
        BalancerResult molecularMassResult = new BalancerResult(false, null,  null);
        molecularMassResult.error = error;
        return molecularMassResult;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}