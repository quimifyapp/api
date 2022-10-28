package com.quimify.api.molecular_mass;

// Esta clase representa las entregas al cliente de una masa molecular.

import java.util.Map;

public class MolecularMassResult {

	private Boolean present;
	private Float molecularMass;
	private Map<String, Float> elementToGrams;
	private Map<String, Integer> elementToMoles;
	private String error;

	// --------------------------------------------------------------------------------

	// Constructores:

	public MolecularMassResult(Float molecularMass, Map<String, Float> elementToGrams,
							   Map<String, Integer> elementToMoles) {
		construir(molecularMass, elementToGrams, elementToMoles);
	}

	public MolecularMassResult(String error) {
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
