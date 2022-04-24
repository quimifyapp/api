package com.quimify.api.inorganico;

import java.text.Normalizer;
import java.util.ArrayList;

// Esta clase representa los compuestos inorgánicos en memoria, simplificados para hacer búsquedas.

public class InorganicoSearchable {

    private final Integer id;
    private final ArrayList<String> keywords = new ArrayList<>(); // Modificable, no re-atribuible

    // --------------------------------------------------------------------------------

    // TODO: Levenshtein o similar

    // Ej.: (this.keywords[...] = "acidosulfuroso") "acidosu" -> 'true'
    // 'input' debe estar previamente normalizado
    public boolean puedeCompletar(String input) {
        for(String keyword : keywords)
            if(keyword.length() >= input.length())
                if(keyword.substring(0, input.length()).contentEquals(input))
                    return true;

        return false;
    }

    // Ej.: (this.keywords[...] = "acidosulfuroso") "acidosulfuroso" -> 'true', "acidosu" -> 'false'
    // 'input' debe estar previamente normalizado
    public boolean coincide(String input) {
        for(String keyword : keywords)
            if(keyword.equals(input))
                    return true;

        return false;
    }

    // Ej.: "Óxido de hierro (III)" -> "oxidodehierroiii"
    public static String normalizar(String input) {
        input = Normalizer.normalize(input, Normalizer.Form.NFD);
        input = input.replaceAll("[^\\p{ASCII}]", "");
        input = input.replaceAll("[^A-Za-z0-9]", "").toLowerCase();

        return input;
    }

    // Constructor:

    public InorganicoSearchable(InorganicoModel inorganico) {
        this.id = inorganico.getId();

        keywords.add(normalizar(inorganico.getFormula()));
        keywords.add(normalizar(inorganico.getNombre()));

        String alternativo = inorganico.getAlternativo();
        if(alternativo != null)
            keywords.add(normalizar(inorganico.getAlternativo()));

        ArrayList<String> etiquetas = inorganico.getEtiquetas();
        if(etiquetas != null)
            keywords.addAll(etiquetas); // Las etiquetas ya están normalizadas
    }

    // Getters:

    public Integer getId() {
        return id;
    }

}
