package com.quimify.api.inorganic;

// Esta clase representa las entregas al cliente de un compuesto inorgánico.

import java.util.ArrayList;
import java.util.List;

class InorganicResult {

    private Boolean present;

    // Si 'encontrado' = true:

    private String formula; // "MgH2"
    private String name; // "hidruro de magnesio
    private String alternativeName; // "dihidruro de magnesio"
    private Float molecularMass; // (g)
    private String density; // (g/cm³)
    private String meltingPoint; // (K)
    private String boilingPoint; // (K)

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

    public Boolean getPresent() {
        return present;
    }

    public void setPresent(Boolean present) {
        this.present = present;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlternativeName() {
        return alternativeName;
    }

    public void setAlternativeName(String alternativeName) {
        this.alternativeName = alternativeName;
    }

    public Float getMolecularMass() {
        return molecularMass;
    }

    public void setMolecularMass(Float molecularMass) {
        this.molecularMass = molecularMass;
    }

    public String getDensity() {
        return density;
    }

    public void setDensity(String density) {
        this.density = density;
    }

    public String getMeltingPoint() {
        return meltingPoint;
    }

    public void setMeltingPoint(String meltingPoint) {
        this.meltingPoint = meltingPoint;
    }

    public String getBoilingPoint() {
        return boilingPoint;
    }

    public void setBoilingPoint(String boilingPoint) {
        this.boilingPoint = boilingPoint;
    }

}
