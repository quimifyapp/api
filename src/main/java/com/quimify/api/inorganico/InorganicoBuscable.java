package com.quimify.api.inorganico;

import java.text.Normalizer;
import java.util.ArrayList;

// Esta clase representa los compuestos inorgánicos en memoria, simplificados para hacer búsquedas.

public class InorganicoBuscable {

    private final Integer id;
    private final ArrayList<String> keywords = new ArrayList<>(); // Modificable, no re-atribuible

    // --------------------------------------------------------------------------------

    // TODO: Levenshtein o similar

    // 'input' debe estar previamente normalizado
    // Ej.: (this.keywords[i] = "acidosulfuroso") "acidosu" -> 'true'
    public boolean puedeCompletar(String input) {
        for(String keyword : keywords)
            if(keyword.length() >= input.length())
                if(keyword.substring(0, input.length()).contentEquals(input))
                    return true;

        return false;
    }

    // 'input' debe estar previamente normalizado
    // Ej.: (this.keywords[i] = "h2so4") "h2so4" -> 'true', "h2so" -> 'false'
    public boolean coincide(String input) {
        for(String keyword : keywords)
            if(keyword.equals(input))
                return true;

        return false;
    }

    // TODO: Comentar esta función
    // Ej.: "Óxido de hierro (III)" -> "oxidodehierroiii"
    public static String normalizar(String input) {
        input = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .replaceAll("[^A-Za-z0-9]", "")
                .toLowerCase();

        return input;
    }

    // Constructor:

    public InorganicoBuscable(InorganicoModel inorganico) {
        id = inorganico.getId();

        keywords.add(normalizar(inorganico.getFormula()));
        keywords.add(normalizar(inorganico.getNombre()));

        if(inorganico.getAlternativo() != null)
            keywords.add(normalizar(inorganico.getAlternativo()));

        if(inorganico.getEtiquetas() != null)
            keywords.addAll(inorganico.getEtiquetas()); // Ya deben estar normalizadas
    }

    // Getters:

    public Integer getId() {
        return id;
    }

}
