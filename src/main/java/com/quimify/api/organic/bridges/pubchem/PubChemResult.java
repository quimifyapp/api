package com.quimify.api.organic.bridges.pubchem;

public class PubChemResult {

	private String url_2d;
	private String masa;
	private String nombre_ingles;

	// Getters y setters:

	public String getMasa() {
		return masa;
	}

	public void setMasa(String masa) {
		this.masa = masa;
	}

	public String getUrl_2d() {
		return url_2d;
	}

	public void setUrl_2d(String url_2d) {
		this.url_2d = url_2d;
	}

	public String getNombre_ingles() {
		return nombre_ingles;
	}

	public void setNombre_ingles(String nombre_ingles) {
		this.nombre_ingles = nombre_ingles;
	}

}
