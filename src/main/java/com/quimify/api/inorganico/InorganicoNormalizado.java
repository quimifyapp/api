package com.quimify.api.inorganico;

import com.quimify.api.Normalizado;
import java.util.Set;

// Esta clase representa los compuestos inorgánicos en memoria y simplificados para autocompletar rápido.

public class InorganicoNormalizado {

    private final String formula_original, nombre_original, alternativo_original;
    private final String formula, nombre, alternativo;
    private final Set<String> etiquetas; // Modificable, no re-atribuible

    public InorganicoNormalizado(InorganicoModel inorganico) {
        formula_original = inorganico.getFormula();
        nombre_original = inorganico.getNombre();
        alternativo_original = inorganico.getAlternativo();

        formula = Normalizado.of(formula_original);
        nombre = Normalizado.of(nombre_original);
        alternativo = Normalizado.of(alternativo_original);

        etiquetas = inorganico.getEtiquetasString(); // Ya deben estar normalizadas
    }

    private boolean completa(String total, String input) {
        return total.length() >= input.length() && total.startsWith(input);
    }

    // Consultas:

    public boolean completaFormula(String input) {
        return completa(formula, input);
    }

    public boolean completaAlternativo(String input) {
        return alternativo != null && completa(alternativo, input);
    }

    public boolean completaNombre(String input) {
        return completa(nombre, input);
    }

    public boolean completanEtiquetas(String input) {
        for (String etiqueta : etiquetas)
            if (completa(etiqueta, input))
                return true;

        return false;
    }

    // Getters:

    public String getFormulaOriginal() {
        return formula_original;
    }

    public String getNombreOriginal() {
        return nombre_original;
    }

    public String getAlternativoOriginal() {
        return alternativo_original;
    }

}