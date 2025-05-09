package com.quimify.api.inorganic;

// This POJO class represents responses of inorganic compounds to the client.

import com.quimify.api.classification.Classification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InorganicResult {

    private boolean found;

    // If not found:

    private Classification classification; // "organicName", null...

    // If found:

    private String suggestion; // "selenuro de potasio", null...

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
        return new InorganicResult();
    }

    // Text:

    @Override
    public String toString() {
        List<String> identifiers = new ArrayList<>();

        if (classification != null)
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

    // Getters and setters (must be defined and public to enable JSON serialization):

    @SuppressWarnings("unused")
    public boolean isFound() {
        return found;
    }

    @SuppressWarnings("unused")
    public void setFound(boolean found) {
        this.found = found;
    }

    @SuppressWarnings("unused")
    public Classification getClassification() {
        return classification;
    }

    @SuppressWarnings("unused")
    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    @SuppressWarnings("unused")
    public String getSuggestion() {
        return suggestion;
    }

    @SuppressWarnings("unused")
    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    @SuppressWarnings("unused")
    public String getFormula() {
        return formula;
    }

    @SuppressWarnings("unused")
    public void setFormula(String formula) {
        this.formula = formula;
    }

    @SuppressWarnings("unused")
    public String getStockName() {
        return stockName;
    }

    @SuppressWarnings("unused")
    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    @SuppressWarnings("unused")
    public String getSystematicName() {
        return systematicName;
    }

    @SuppressWarnings("unused")
    public void setSystematicName(String systematicName) {
        this.systematicName = systematicName;
    }

    @SuppressWarnings("unused")
    public String getTraditionalName() {
        return traditionalName;
    }

    @SuppressWarnings("unused")
    public void setTraditionalName(String traditionalName) {
        this.traditionalName = traditionalName;
    }

    @SuppressWarnings("unused")
    public String getCommonName() {
        return commonName;
    }

    @SuppressWarnings("unused")
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    @SuppressWarnings("unused")
    public String getMolecularMass() {
        return molecularMass;
    }

    @SuppressWarnings("unused")
    public void setMolecularMass(String molecularMass) {
        this.molecularMass = molecularMass;
    }

    @SuppressWarnings("unused")
    public String getDensity() {
        return density;
    }

    @SuppressWarnings("unused")
    public void setDensity(String density) {
        this.density = density;
    }

    @SuppressWarnings("unused")
    public String getMeltingPoint() {
        return meltingPoint;
    }

    @SuppressWarnings("unused")
    public void setMeltingPoint(String meltingPoint) {
        this.meltingPoint = meltingPoint;
    }

    @SuppressWarnings("unused")
    public String getBoilingPoint() {
        return boilingPoint;
    }

    @SuppressWarnings("unused")
    public void setBoilingPoint(String boilingPoint) {
        this.boilingPoint = boilingPoint;
    }

}
