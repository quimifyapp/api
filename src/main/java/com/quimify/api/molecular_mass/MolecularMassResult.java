package com.quimify.api.molecular_mass;

// Esta clase representa las entregas al cliente de una masa molecular.

import java.util.Map;

class MolecularMassResult {

	private Boolean present;
	private Float molecularMass;
	private Map<String, Float> elementToGrams;
	private Map<String, Integer> elementToMoles;
	private String error;

	// --------------------------------------------------------------------------------

	// Constructores:

	protected MolecularMassResult(Float molecularMass, Map<String, Float> elementToGrams,
							   Map<String, Integer> elementToMoles) {
		construir(molecularMass, elementToGrams, elementToMoles);
	}

	protected MolecularMassResult(String error) {
		this.present = Boolean.FALSE;
		this.error = error;
	}

	private void construir(Float masa, Map<String, Float> elemento_a_gramos, Map<String, Integer> elemento_a_moles) {
		this.present = Boolean.TRUE;
		this.molecularMass = masa;
		this.elementToGrams = elemento_a_gramos;
		this.elementToMoles = elemento_a_moles;
	}

	// Getters y setters:

	protected Boolean getPresent() {
		return present;
	}

	protected void setPresent(Boolean present) {
		this.present = present;
	}

	protected Float getMolecularMass() {
		return molecularMass;
	}

	protected void setMolecularMass(Float molecularMass) {
		this.molecularMass = molecularMass;
	}

	protected Map<String, Float> getElementToGrams() {
		return elementToGrams;
	}

	protected void setElementToGrams(Map<String, Float> elementToGrams) {
		this.elementToGrams = elementToGrams;
	}

	protected Map<String, Integer> getElementToMoles() {
		return elementToMoles;
	}

	protected void setElementToMoles(Map<String, Integer> elementToMoles) {
		this.elementToMoles = elementToMoles;
	}

	protected String getError() {
		return error;
	}

	protected void setError(String error) {
		this.error = error;
	}

}
