package com.quimify.api.inorganic;

// This POJO class represents responses of inorganic compounds to the client.

import com.quimify.api.classification.Classification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class InorganicResult {

    private boolean found;

    // If not found:

    private Classification classification; // "organicName"

    // If found:

    private String suggestion; // "selenuro de potasio"

    private String formula; // "MgH2"

    private String stockName; // "óxido de níquel (III)"
    private String systematicName; // "trióxido de diníquel"
    private String traditionalName; // "óxido niquélico"
    private String commonName; // "potasio"

    private String molecularMass; // (g/mol)
    private String density; // (g/cm³)
    private String meltingPoint; // (K)
    private String boilingPoint; // (K)

    // Constructors:

    InorganicResult(InorganicModel inorganicModel, String suggestion) {
        this.found = true;

        this.suggestion = suggestion;

        this.formula = inorganicModel.getFormula();

        this.stockName = inorganicModel.getStockName();
        this.systematicName = inorganicModel.getSystematicName();
        this.traditionalName = inorganicModel.getTraditionalName();
        this.commonName = inorganicModel.getCommonName();

        this.molecularMass = inorganicModel.getMolecularMass();
        this.density = inorganicModel.getDensity();
        this.meltingPoint = inorganicModel.getMeltingPoint();
        this.boilingPoint = inorganicModel.getBoilingPoint();
    }

    InorganicResult(InorganicModel inorganicModel) {
        this(inorganicModel, null);
    }

    private InorganicResult(Classification classification) {
        this.found = false;
        this.classification = classification;
    }

    static InorganicResult classification(Classification classification) {
        return new InorganicResult(classification);
    }

    private InorganicResult() {
        this.found = false;
    }

    static InorganicResult notFound() {
        return new InorganicResult((Classification) null);
    }

    // Text:

    @Override
    public String toString() {
        List<String> identifiers = new ArrayList<>();

        identifiers.add(classification.toString());
        identifiers.add(suggestion);

        identifiers.add(formula);
        identifiers.add(stockName);
        identifiers.add(systematicName);
        identifiers.add(traditionalName);
        identifiers.add(commonName);

        identifiers.removeIf(Objects::isNull);

        return identifiers.toString();
    }

    // Getters and setters (must be public to enable JSON serialization):

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public Classification getClassification() {
        return classification;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
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

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
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
