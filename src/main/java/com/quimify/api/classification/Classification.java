package com.quimify.api.classification;

import java.util.Optional;

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

    // Constructors:

    Classification(String suggestion) {
        this.suggestion = suggestion;
    }

    static Optional<Classification> ofName(String name) {
        for (Classification classification : Classification.values())
            if(classification.suggestion.equals(name))
                return Optional.of(classification);

        return Optional.empty();
    }

    // Queries:

    @Override
    public String toString() {
        return suggestion;
    }

}
