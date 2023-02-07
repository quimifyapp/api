package com.quimify.api.inorganic;

// This POJO class represents responses of inorganic compounds to the client.

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class InorganicResult {

    private boolean present;

    // If present is true

    private String formula; // "MgH2"

    private String stockName; // "óxido de níquel (III)"
    private String systematicName; // "trióxido de diníquel"
    private String traditionalName; // "óxido niquélico"
    private String otherName; // "potasio"

    private String molecularMass; // (g)
    private String density; // (g/cm³)
    private String meltingPoint; // (K)
    private String boilingPoint; // (K)

    // Constants:

    protected static final InorganicResult notFound = new InorganicResult();

    // Constructors:

    protected InorganicResult(InorganicModel inorganicModel) {
        this.present = true;
        this.formula = inorganicModel.getFormula();

        this.stockName = inorganicModel.getStockName();
        this.systematicName = inorganicModel.getSystematicName();
        this.traditionalName = inorganicModel.getTraditionalName();
        this.otherName = inorganicModel.getCommonName();

        this.molecularMass = inorganicModel.getMolecularMass();
        this.density = inorganicModel.getDensity();
        this.meltingPoint = inorganicModel.getMeltingPoint();
        this.boilingPoint = inorganicModel.getBoilingPoint();
    }

    private InorganicResult() {
        this.present = false;
    }

    // Texto:

    @Override
    public String toString() {
        List<String> identifiers = new ArrayList<>();

        identifiers.add(formula);
        identifiers.add(stockName);
        identifiers.add(systematicName);
        identifiers.add(traditionalName);
        identifiers.add(otherName);

        identifiers.removeIf(Objects::isNull);

        return identifiers.toString();
    }

    // Getters y setters:

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
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
