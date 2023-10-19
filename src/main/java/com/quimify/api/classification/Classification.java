package com.quimify.api.classification;

public enum Classification {
    inorganicFormula("inorganic-formula"), // 0 in DB
    organicFormula("organic-formula"), // 1 in DB
    inorganicName("inorganic-name"), // 2 in DB
    organicName("organic-name"), // 3 in DB
    nomenclatureProblem("nomenclature-problem"), // 4 in DB
    molecularMassProblem("molecular-mass-problem"), // 5 in DB
    chemicalProblem("chemical-problem"), // 6 in DB
    chemicalReaction("chemical-reaction"); // 7 in DB

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
