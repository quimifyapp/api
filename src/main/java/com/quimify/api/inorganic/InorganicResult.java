package com.quimify.api.inorganic;

// Esta clase representa las entregas al cliente de un compuesto inorgánico.

import java.util.ArrayList;
import java.util.List;

class InorganicResult {

    private Boolean present;

    // Si 'encontrado' = true:

    private String formula; // "MgH2"

    private String stockName; // "óxido de níquel (III)"
    private String systematicName; // "trióxido de diníquel"
    private String traditionalName; // "óxido niquélico"
    private String otherName; // "potasio"

    private String molecularMass; // (g)
    private String density; // (g/cm³)
    private String meltingPoint; // (K)
    private String boilingPoint; // (K)

    // --------------------------------------------------------------------------------

    // Constructores:

    protected InorganicResult(InorganicModel inorganico) {
        this.present = true;
        build(inorganico);
    }

    protected InorganicResult() {
        this.present = false;
    }

    private void build(InorganicModel inorganicModel) {
        this.formula = inorganicModel.getFormula();

        this.stockName = inorganicModel.getStockName();
        this.systematicName = inorganicModel.getSystematicName();
        this.traditionalName = inorganicModel.getTraditionalName();
        this.otherName = inorganicModel.getOtherName();

        this.molecularMass = inorganicModel.getMolecularMass();
        this.density = inorganicModel.getDensity();
        this.meltingPoint = inorganicModel.getMeltingPoint();
        this.boilingPoint = inorganicModel.getBoilingPoint();
    }

    // Texto:

    @Override
    public String toString() {
        List<String> words = new ArrayList<>();

        words.add(formula);

        if(stockName != null)
            words.add(stockName);

        if(systematicName != null)
            words.add(systematicName);

        if(traditionalName != null)
            words.add(traditionalName);

        if(otherName != null)
            words.add(otherName);

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

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getSystematicName() {
        return systematicName;
    }

    public void setSystematicName(String systematicName) {
        this.systematicName = systematicName;
    }

    public String getTraditionalName() {
        return traditionalName;
    }

    public void setTraditionalName(String traditionalName) {
        this.traditionalName = traditionalName;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public String getMolecularMass() {
        return molecularMass;
    }

    public void setMolecularMass(String molecularMass) {
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
