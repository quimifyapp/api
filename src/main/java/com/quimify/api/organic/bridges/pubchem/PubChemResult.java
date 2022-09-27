package com.quimify.api.organic.bridges.pubchem;

import java.util.Optional;

public class PubChemResult {

	private String url_2d;
	private Optional<String> masa = Optional.empty();
	private Optional<String> nombre_ingles = Optional.empty();

	// Getters y setters:

	public Optional<String> getMasa() {
		return masa;
	}

	public void setMasa(String masa) {
		this.masa = Optional.ofNullable(masa);
	}

	public String getUrl_2d() {
		return url_2d;
	}

	public void setUrl_2d(String url_2d) {
		this.url_2d = url_2d;
	}

	public Optional<String> getNombre_ingles() {
		return nombre_ingles;
	}

	public void setNombre_ingles(String nombre_ingles) {
		this.nombre_ingles = Optional.ofNullable(nombre_ingles);
	}

}
