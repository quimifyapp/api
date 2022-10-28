package com.quimify.api.inorganic;

import com.quimify.api.Normalized;
import java.util.Set;

// Esta clase representa los compuestos inorgánicos en memoria y simplificados para autocompletar rápido.

public class NormalizedInorganic {

    private final String originalFormula, originalName, originalAlternativeName;
    private final String formula, name, alternativeName;
    private final Set<String> searchTags; // Modificable, no re-atribuible

    public NormalizedInorganic(InorganicModel inorganic) {
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

    public boolean formulaCanComplete(String input) {
        return canComplete(formula, input);
    }

    public boolean alternativeNameCanComplete(String input) {
        return alternativeName != null && canComplete(alternativeName, input);
    }

    public boolean nameCanComplete(String input) {
        return canComplete(name, input);
    }

    public boolean searchTagsCanComplete(String input) {
        for (String searchTag : searchTags)
            if (canComplete(searchTag, input))
                return true;

        return false;
    }

    // Getters:

    public String getOriginalFormula() {
        return originalFormula;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getOriginalAlternativeName() {
        return originalAlternativeName;
    }

}