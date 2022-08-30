package com.quimify.servidor.masa_molecular;

// Esta clase representa las entregas al cliente de una masa molecular.

import java.util.Map;

public class MasaMolecularResultado {

	private Boolean encontrado;
	private Float masa;
	private Map<String, Float> elemento_a_gramos;
	private Map<String, Integer> elemento_a_moles;
	private String error;

	// --------------------------------------------------------------------------------

	// Constructores:

	public MasaMolecularResultado(Float masa, Map<String, Float> elemento_a_gramos,
								  Map<String, Integer> elemento_a_moles) {
		construir(masa, elemento_a_gramos, elemento_a_moles);
	}

	public MasaMolecularResultado(String error) {
		this.encontrado = Boolean.FALSE;
		this.error = error;
	}

	private void construir(Float masa, Map<String, Float> elemento_a_gramos, Map<String, Integer> elemento_a_moles) {
		this.encontrado = Boolean.TRUE;
		this.masa = masa;
		this.elemento_a_gramos = elemento_a_gramos;
		this.elemento_a_moles = elemento_a_moles;
	}

	// Getters y setters:


	public Boolean getEncontrado() {
		return encontrado;
	}

	public void setEncontrado(Boolean encontrado) {
		this.encontrado = encontrado;
	}

	public Float getMasa() {
		return masa;
	}

	public void setMasa(Float masa) {
		this.masa = masa;
	}

	public Map<String, Float> getElemento_a_gramos() {
		return elemento_a_gramos;
	}

	public void setElemento_a_gramos(Map<String, Float> elemento_a_gramos) {
		this.elemento_a_gramos = elemento_a_gramos;
	}

	public Map<String, Integer> getElemento_a_moles() {
		return elemento_a_moles;
	}

	public void setElemento_a_moles(Map<String, Integer> elemento_a_moles) {
		this.elemento_a_moles = elemento_a_moles;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
