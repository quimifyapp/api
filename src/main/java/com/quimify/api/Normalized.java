package com.quimify.api;

import org.springframework.lang.Nullable;

import java.text.Normalizer;

// Esta clase se encarga de normalizar texto.

public class Normalized {

    // Ej.: "Óxido de hierro (III)" -> "oxidodehierroiii"
    public static String of(@Nullable String texto) {
        if (texto == null)
            return null;

        return Normalizer.normalize(texto, Normalizer.Form.NFD) // Sin acentos ni diacríticos
                .replaceAll("[^\\p{ASCII}]", "") // Solo ASCII
                .replaceAll("[^A-Za-z0-9]", "") // Solo alfanumérico
                .toLowerCase(); // Solo minúsculas
    }

}
