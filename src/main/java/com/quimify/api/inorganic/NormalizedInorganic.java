package com.quimify.api.inorganic;

import com.quimify.api.Normalized;
import java.util.Set;

// Esta clase representa los compuestos inorgánicos en memoria y simplificados para autocompletar rápido.

class NormalizedInorganic {

    private final String originalFormula, originalName, originalAlternativeName;
    private final String formula, name, alternativeName;
    private final Set<String> searchTags; // Modificable, no re-atribuible

    protected NormalizedInorganic(InorganicModel inorganic) {
        originalFormula = inorganic.getFormula();
        originalName = inorganic.getName();
        originalAlternativeName = inorganic.getAlternativeName();

        formula = Normalized.of(originalFormula);
        name = Normalized.of(originalName);
        alternativeName = Normalized.of(originalAlternativeName);

        searchTags = inorganic.getEtiquetasString(); // Ya deben estar normalizadas
    }

    private static boolean canComplete(String total, String input) {
        return total.length() >= input.length() && total.startsWith(input);
    }

    // Consultas:

    protected boolean formulaCanComplete(String input) {
        return canComplete(formula, input);
    }

    protected boolean alternativeNameCanComplete(String input) {
        return alternativeName != null && canComplete(alternativeName, input);
    }

    protected boolean nameCanComplete(String input) {
        return canComplete(name, input);
    }

    protected boolean searchTagsCanComplete(String input) {
        for (String searchTag : searchTags)
            if (canComplete(searchTag, input))
                return true;

        return false;
    }

    // Getters:

    protected String getOriginalFormula() {
        return originalFormula;
    }

    protected String getOriginalName() {
        return originalName;
    }

    protected String getOriginalAlternativeName() {
        return originalAlternativeName;
    }

}