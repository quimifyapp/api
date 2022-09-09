package com.quimify.api.autorizacion;

public class Autorizacion {

	private static final String CLAVE_PUBLICA = "qTsXzmRYeM8Bf7uZbK7Df2EyauzZYTm7";
	private static final String CLAVE_PRIVADA = "qTsXzmRYeM8Bf7uZbK7Df2EyauzZYTm7";

	// --------------------------------------------------------------------------------

	public static boolean esClavePublica(String clave) {
		return clave.contentEquals(CLAVE_PUBLICA);
	}

	public static boolean esClavePrivada(String clave) {
		return clave.contentEquals(CLAVE_PRIVADA);
	}

}
