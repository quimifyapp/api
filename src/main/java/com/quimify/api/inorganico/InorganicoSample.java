package com.quimify.api.inorganico;

import java.text.Normalizer;
import java.util.ArrayList;

// Esta clase representa los compuestos inorgánicos que están en memoria para realizar búsquedas.
// (id, fórmula, nombre) ~ (alternativo) ~ (etiqueta0, etiqueta1...)

public class InorganicoSample {

    private final Integer id;
    private ArrayList<String> keywords = new ArrayList<>();

    // TODO: Levenshtein o similar

    // "Óxido de hierro (III)" -> "oxidodehierroiii"
    private String normalizar(String input) {
        input = Normalizer.normalize(input, Normalizer.Form.NFD);
        input = input.replaceAll("[^\\p{ASCII}]", "");
        input = input.replaceAll("[^A-Za-z0-9]", "").toLowerCase();

        return input;
    }

    public InorganicoSample(InorganicoModel inorganico) {
        this.id = inorganico.getId();

        keywords.add(normalizar(inorganico.getFormula()));
        keywords.add(normalizar(inorganico.getNombre()));

        String alternativo = inorganico.getAlternativo();
        if(alternativo != null)
            keywords.add(normalizar(alternativo));

        ArrayList<String> etiquetas = inorganico.getEtiquetas();
        if(etiquetas != null)
            keywords.addAll(etiquetas); // Las etiquetas ya están normalizadas
    }
}
