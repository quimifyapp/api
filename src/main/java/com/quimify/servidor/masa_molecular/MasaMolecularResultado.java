package com.quimify.servidor.masa_molecular;

// Esta clase representa las entregas al cliente de una masa molecular.

import java.util.Map;

public class MasaMolecularResultado {

	private Boolean encontrado;
	private String masa_molecular;
	private Map<String, Integer> elemento_a_moles;
	private Map<String, Float> elemento_a_gramos;
	private String error;

	// --------------------------------------------------------------------------------

	// Constructores:

	public MasaMolecularResultado(String masa_molecular, Map<String, Integer> elemento_a_moles,
								  Map<String, Float> elemento_a_gramos) {
		construir(masa_molecular, elemento_a_moles, elemento_a_gramos);
	}

	public MasaMolecularResultado(String masa_molecular, Map<String, Integer> elemento_a_moles) {
		construir(masa_molecular, elemento_a_moles, null);
	}

	public MasaMolecularResultado(String error) {
		this.encontrado = Boolean.FALSE;
		this.error = error;
	}

	private void construir(String masa_molecular, Map<String, Integer> elemento_a_moles,
						   Map<String, Float> elemento_a_gramos) {
		this.encontrado = Boolean.TRUE;
		this.masa_molecular = masa_molecular;
		this.elemento_a_moles = elemento_a_moles;
		this.elemento_a_gramos = elemento_a_gramos;
	}

	// Getters y setters:

	public Boolean getEncontrado() {
		return encontrado;
	}

	public void setEncontrado(Boolean encontrado) {
		this.encontrado = encontrado;
	}

	public String getMasa_molecular() {
		return masa_molecular;
	}

	public void setMasa_molecular(String masa_molecular) {
		this.masa_molecular = masa_molecular;
	}

	public Map<String, Integer> getElemento_a_moles() {
		return elemento_a_moles;
	}

	public void setElemento_a_moles(Map<String, Integer> elemento_a_moles) {
		this.elemento_a_moles = elemento_a_moles;
	}

	public Map<String, Float> getElemento_a_gramos() {
		return elemento_a_gramos;
	}

	public void setElemento_a_gramos(Map<String, Float> elemento_a_gramos) {
		this.elemento_a_gramos = elemento_a_gramos;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
