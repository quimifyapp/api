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
        // TODO
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

    // Getters and setters (must be defined and public to enable JSON serialization):

    @SuppressWarnings("unused")
    public boolean isPresent() {
        return present;
    }

    @SuppressWarnings("unused")
    public void setPresent(boolean present) {
        this.present = present;
    }

    @SuppressWarnings("unused")
    public Float getMolecularMass() {
        return molecularMass;
    }

    @SuppressWarnings("unused")
    public void setMolecularMass(Float molecularMass) {
        this.molecularMass = molecularMass;
    }

    @SuppressWarnings("unused")
    public Map<String, Float> getElementToGrams() {
        return elementToGrams;
    }

    @SuppressWarnings("unused")
    public void setElementToGrams(Map<String, Float> elementToGrams) {
        this.elementToGrams = elementToGrams;
    }

    @SuppressWarnings("unused")
    public Map<String, Integer> getElementToMoles() {
        return elementToMoles;
    }

    @SuppressWarnings("unused")
    public void setElementToMoles(Map<String, Integer> elementToMoles) {
        this.elementToMoles = elementToMoles;
    }

    @SuppressWarnings("unused")
    public String getError() {
        return error;
    }

    @SuppressWarnings("unused")
    public void setError(String error) {
        this.error = error;
    }

}
