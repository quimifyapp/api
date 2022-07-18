package com.quimify.api.inorganico;

import com.quimify.api.Normalizar;

import java.util.ArrayList;
import java.util.List;

// Esta clase representa los compuestos inorgánicos en memoria, simplificados para hacer búsquedas.

public class InorganicoBuscable {

    private final Integer id;
    private final List<String> keywords = new ArrayList<>(); // Modificable, no re-atribuible

    // --------------------------------------------------------------------------------

    // TODO: Levenshtein o similar

    // 'input' debe estar previamente normalizado
    // Ej.: (this.keywords[i] = "acidosulfuroso") "acidosu" -> "acidosulfuroso"
    public String autoCompletar(String input) {
        for(String keyword : keywords)
            if(keyword.length() >= input.length())
                if(keyword.startsWith(input))
                    return keyword;

        return null;
    }

    // Pre.: 'input' debe estar previamente normalizado
    // Ej.: (this.keywords[i] = "h2so4") "h2so4" -> 'true', "h2so" -> 'false'
    public boolean coincide(String input) {
        for(String keyword : keywords)
            if(keyword.equals(input))
                return true;

        return false;
    }

    // Constructor:

    public InorganicoBuscable(InorganicoModel inorganico) {
        id = inorganico.getId();

        keywords.add(new Normalizar(inorganico.getFormula()).get());
        keywords.add(new Normalizar(inorganico.getNombre()).get());

        if(inorganico.getAlternativo() != null)
            keywords.add(new Normalizar(inorganico.getAlternativo()).get());

        if(inorganico.getEtiquetas() != null)
            for(EtiquetaModel etiqueta : inorganico.getEtiquetas())
                keywords.add(etiqueta.getTexto_normalizado()); // Ya deben estar normalizadas
    }

    // Getters:

    public Integer getId() {
        return id;
    }

}
