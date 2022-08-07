package com.quimify.servidor;

import java.text.Normalizer;

// Esta clase se encarga de normalizar texto.

public class Normalizar {

    String texto_normalizado;

    // Ej.: "Óxido de hierro (III)" -> "oxidodehierroiii"
    public Normalizar(String texto) {
        if(texto != null)
            texto_normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD) // Sin acentos ni diacríticos
                .replaceAll("[^\\p{ASCII}]", "") // Solo ASCII
                .replaceAll("[^A-Za-z0-9]", "") // Solo alfanumérico
                .toLowerCase(); // Solo minúsculas
    }

    public String get() {
        return texto_normalizado;
    }

}
