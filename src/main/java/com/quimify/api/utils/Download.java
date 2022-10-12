package com.quimify.api.utils;

// Esta clase procesa las conexiones con otros servidores.

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Download {

	private HttpURLConnection httpURLConnection;

	// Constructores:

	private void construir(String url) throws IOException {
		httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
		httpURLConnection.setRequestMethod("GET");
	}

	public Download(String url, String parametro) throws IOException {
		construir(url + formatearHTTP(parametro));
	}

	public Download(String url) throws IOException {
		construir(url);
	}

	// Públicos:

	public void setPropiedad(String key, String valor) {
		httpURLConnection.setRequestProperty(key, valor);
	}

	public String getTexto() throws IOException {
		BufferedReader descarga = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

		String linea;
		StringBuilder texto = new StringBuilder();
		while((linea = descarga.readLine()) != null)
			texto.append(linea);

		descarga.close();
		httpURLConnection.disconnect();

		return texto.toString();
	}

	// Privados:

	public static String formatearHTTP(String input) {
		return URLEncoder.encode(input, StandardCharsets.UTF_8);
	}

}
