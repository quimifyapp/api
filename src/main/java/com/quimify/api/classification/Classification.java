package com.quimify.api.classification;

public enum Classification {
    inorganicFormula("inorganic-formula"), // 0
    organicFormula("organic-formula"), // 1
    inorganicName("inorganic-name"), // 2
    organicName("organic-name"), // 3
    molecularMassProblem("molecular-mass-problem"), // 4
    chemicalProblem("chemical-problem"), // 5
    chemicalReaction("chemical-reaction"); // 6

    private final String suggestion;

    // Constructor:

    Classification(String suggestion) {
        this.suggestion = suggestion;
    }

    // Queries:

    @Override
    public String toString() {
        return suggestion;
    }

}
