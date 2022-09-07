package com.quimify.api.autentificacion;

public class Autentificacion {

	private static final String CLAVE_PUBLICA = "qTsXzmRYeM8Bf7uZbK7Df2EyauzZYTm7";
	private static final String CLAVE_PRIVADA = "m2H8JBfCJ6W9wQNLkTLvekLwt11uJbj4";

	// --------------------------------------------------------------------------------

	public static boolean esClavePublica(String clave) {
		return clave.contentEquals(CLAVE_PUBLICA);
	}

	public static boolean esClavePrivada(String clave) {
		return clave.contentEquals(CLAVE_PRIVADA);
	}

}
