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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public Float getMolecularMass() {
        return molecularMass;
    }

    public void setMolecularMass(Float molecularMass) {
        this.molecularMass = molecularMass;
    }

    public String getUrl2D() {
        return url2D;
    }

    public void setUrl2D(String url2D) {
        this.url2D = url2D;
    }

    public String getUrl3D() {
        return url3D;
    }

    public void setUrl3D(String url3D) {
        this.url3D = url3D;
    }

}
