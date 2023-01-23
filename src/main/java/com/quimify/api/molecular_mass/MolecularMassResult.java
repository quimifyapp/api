package com.quimify.api.molecular_mass;

// This POJO class represents responses of molecular masses to the client.

import java.util.Map;

class MolecularMassResult {

	private boolean present;
	private Float molecularMass;
	private Map<String, Float> elementToGrams;
	private Map<String, Integer> elementToMoles;
	private String error;

	// Constructors:

	protected MolecularMassResult(Float molecularMass, Map<String, Float> elementToGrams,
								  Map<String, Integer> elementToMoles) {
		this.present = true;
		this.molecularMass = molecularMass;
		this.elementToGrams = elementToGrams;
		this.elementToMoles = elementToMoles;
	}

	protected MolecularMassResult(String error) {
		this.present = false;
		this.error = error;
	}

	// Getters and setters:

	public Boolean getPresent() {
		return present;
	}

	public void setPresent(Boolean present) {
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
