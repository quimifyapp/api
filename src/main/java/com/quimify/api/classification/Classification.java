package com.quimify.api.classification;

public enum Classification {
    inorganicFormula("inorganicFormula"),
    organicFormula("organicFormula"),
    inorganicName("inorganicName"),
    organicName("organicName"),
    nomenclatureProblem("nomenclatureProblem"),
    molecularMassProblem("molecularMassProblem"),
    chemicalProblem("chemicalProblem"),
    chemicalReaction("chemicalReaction");

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
