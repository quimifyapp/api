package com.quimify.api.classifier;

public enum ClassifierResult {
    inorganicFormula("inorganic-formula"),
    organicFormula("organic-formula"),
    inorganicName("inorganic-name"),
    organicName("organic-name"),
    molecularMass("molecular-mass"),
    chemicalReaction("chemical-reaction"),
    chemistryProblem("chemistry-problem");

    private final String suggestion;

    // Constructor:

    ClassifierResult(String suggestion) {
        this.suggestion = suggestion;
    }

    // Queries:

    @Override
    public String toString() {
        return suggestion;
    }

}

