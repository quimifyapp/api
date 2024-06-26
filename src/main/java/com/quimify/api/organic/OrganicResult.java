package com.quimify.api.organic;

// This POJO class represents responses of organic compounds to the client.

import com.quimify.api.classification.Classification;

class OrganicResult {

    private boolean found;

    private Classification classification; // "inorganicFormula", null...

    private String suggestion; // "hexano", null...

    private String name; // "2,2-dicloroetil metil éter"
    private String structure; // "CH3-O-CH2-CHCl2"
    private Float molecularMass; // "128.98" (g/mol)
    private String url2D; // "https://pubchem.ncbi.nlm.nih.gov/image/imagefly.cgi?width=500&height=500&cid=118742"
    private String url3D; // "https://pubchem.ncbi.nlm.nih.gov/compound/118742#section=3D-Conformer&fullscreen=true"

    // Constructors:

    OrganicResult(String name, String structure) {
        this.found = true;
        this.name = name;
        this.structure = structure;
    }

    private OrganicResult() {
        this.found = false;
    }

    static OrganicResult notFound() {
        return new OrganicResult();
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
    public String getName() {
        return name;
    }

    @SuppressWarnings("unused")
    public void setName(String name) {
        this.name = name;
    }

    @SuppressWarnings("unused")
    public String getStructure() {
        return structure;
    }

    @SuppressWarnings("unused")
    public void setStructure(String structure) {
        this.structure = structure;
    }

    @SuppressWarnings("unused")
    public Float getMolecularMass() {
        return molecularMass;
    }

    @SuppressWarnings("unused")
    public void setMolecularMass(Float molecularMass) {
        this.molecularMass = molecularMass;
    }

    @SuppressWarnings("unused")
    public String getUrl2D() {
        return url2D;
    }

    @SuppressWarnings("unused")
    public void setUrl2D(String url2D) {
        this.url2D = url2D;
    }

    @SuppressWarnings("unused")
    public String getUrl3D() {
        return url3D;
    }

    @SuppressWarnings("unused")
    public void setUrl3D(String url3D) {
        this.url3D = url3D;
    }

}
