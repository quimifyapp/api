package com.quimify.api.molecularmass;

// This POJO class represents responses of molecular masses to the client.

import java.util.Map;

class MolecularMassResult {

    private boolean present;

    private String suggestion; // "CH3CH2CH3" or an input kind like "inorganic-name"

    // If present is true:

    private Float molecularMass;
    private Map<String, Float> elementToGrams;
    private Map<String, Integer> elementToMoles;

    private String error;

    // Constructors:

    MolecularMassResult(boolean present, Float molecularMass, Map<String, Float> elementToGrams,
                        Map<String, Integer> elementToMoles, String suggestion) {
        this.present = present;
        this.suggestion = suggestion;
        this.molecularMass = molecularMass;
        this.elementToGrams = elementToGrams;
        this.elementToMoles = elementToMoles;
    }

    MolecularMassResult(String suggestion) {
        this.present = false;
        this.suggestion = suggestion;
    }

    static MolecularMassResult error(String error) {
        MolecularMassResult molecularMassResult = new MolecularMassResult(false, null, null, null, null);
        molecularMassResult.error = error;
        return molecularMassResult;
    }

    // Getters and setters (must be public to enable JSON serialization):

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public Float getMolecularMass() {
        return molecularMass;
    }

    public void setMolecularMass(Float molecularMass) {
        this.molecularMass = molecularMass;
    }

    public Map<String, Float> getElementToGrams() {
        return elementToGrams;
    }

    public void setElementToGrams(Map<String, Float> elementToGrams) {
        this.elementToGrams = elementToGrams;
    }

    public Map<String, Integer> getElementToMoles() {
        return elementToMoles;
    }

    public void setElementToMoles(Map<String, Integer> elementToMoles) {
        this.elementToMoles = elementToMoles;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
