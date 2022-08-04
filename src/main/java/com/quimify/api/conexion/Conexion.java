package com.quimify.api.conexion;

// Esta clase procesa las conexiones con otros servidores.

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Conexion {

	private HttpURLConnection conexion;

	// Constructores:

	private void construir(String url) throws IOException {
		conexion = (HttpURLConnection) new URL(url).openConnection();
		conexion.setRequestMethod("GET");
	}

	public Conexion(String url, String parametro) throws IOException {
		construir(url + formatearHTTP(parametro));
	}

	public Conexion(String url) throws IOException {
		construir(url);
	}

	// Públicos:

	public void setPropiedad(String key, String valor) {
		conexion.setRequestProperty(key, valor);
	}

	public String getTexto() throws IOException {
		BufferedReader descarga = new BufferedReader(new InputStreamReader(conexion.getInputStream()));

		String linea;
		StringBuilder texto = new StringBuilder();
		while((linea = descarga.readLine()) != null)
			texto.append(linea);

		descarga.close();
		conexion.disconnect();

		return texto.toString();
	}

	// Privados:

	private String formatearHTTP(String input) {
		return URLEncoder.encode(input, StandardCharsets.UTF_8);
	}

}
