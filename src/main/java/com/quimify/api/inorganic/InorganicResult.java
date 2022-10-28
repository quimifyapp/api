package com.quimify.api.inorganic;

// Esta clase representa las entregas al cliente de un compuesto inorgánico.

import java.util.ArrayList;
import java.util.List;

class InorganicResult {

    private Boolean present;

    // Si 'encontrado' = true:

    private String formula;     // "MgH2"
    private String name;      // "hidruro de magnesio
    private String alternativeName; // "dihidruro de magnesio"
    private Float molecularMass;        // (g)
    private String density;    // (g/cm³)
    private String meltingPoint;      // (K)
    private String boilingPoint;  // (K)

    // --------------------------------------------------------------------------------

    // Constructores:

    protected InorganicResult(InorganicModel inorganico) {
        this.present = true;
        copiar(inorganico);
    }

    protected InorganicResult() {
        this.present = false;
    }

    private void copiar(InorganicModel inorganico) {
        this.formula = inorganico.getFormula();
        this.name = inorganico.getName();
        this.alternativeName = inorganico.getAlternativeName();
        this.molecularMass = inorganico.getMolecularMass();
        this.density = inorganico.getDensity();
        this.meltingPoint = inorganico.getMeltingPoint();
        this.boilingPoint = inorganico.getBoilingPoint();
    }

    // Texto:

    @Override
    public String toString() {
        List<String> words = new ArrayList<>();

        words.add(formula);
        words.add(name);

        if(alternativeName != null)
            words.add(alternativeName);

        return words.toString();
    }

    // Getters y setters:

    protected Boolean getPresent() {
        return present;
    }

    protected void setPresent(Boolean present) {
        this.present = present;
    }

    protected String getFormula() {
        return formula;
    }

    protected void setFormula(String formula) {
        this.formula = formula;
    }

    protected String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected String getAlternativeName() {
        return alternativeName;
    }

    protected void setAlternativeName(String alternativeName) {
        this.alternativeName = alternativeName;
    }

    protected Float getMolecularMass() {
        return molecularMass;
    }

    protected void setMolecularMass(Float molecularMass) {
        this.molecularMass = molecularMass;
    }

    protected String getDensity() {
        return density;
    }

    protected void setDensity(String density) {
        this.density = density;
    }

    protected String getMeltingPoint() {
        return meltingPoint;
    }

    protected void setMeltingPoint(String meltingPoint) {
        this.meltingPoint = meltingPoint;
    }

    protected String getBoilingPoint() {
        return boilingPoint;
    }

    protected void setBoilingPoint(String boilingPoint) {
        this.boilingPoint = boilingPoint;
    }
}
