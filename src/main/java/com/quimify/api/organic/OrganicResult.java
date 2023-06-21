package com.quimify.api.organic;

// This POJO class represents responses of organic compounds to the client.

class OrganicResult {

    private boolean found;

    private String suggestion; // "hexano" or an input kind like "organic-formula"

    // If present is true:

    private String name; // "2,2-dicloroetil metil éter"
    private String structure; // "CH3-O-CH2-CHCl2"
    private Float molecularMass; // (g/mol)
    private String url2D; // "https://pubchem.ncbi.nlm.nih.gov/image/imagefly.cgi?width=500&height=500&cid=118742"

    // Constructors:

    OrganicResult(String name, String structure, Float molecularMass, String url2D, String suggestion) {
        this.found = true;
        this.suggestion = suggestion;
        this.name = name;
        this.structure = structure;
        this.molecularMass = molecularMass;
        this.url2D = url2D;
    }

    OrganicResult(String name, String structure, Float molecularMass, String url2D) {
        this(name, structure, molecularMass, url2D, null);
    }

    OrganicResult(String suggestion) {
        this.found = false;
        this.suggestion = suggestion;
    }

    static OrganicResult notFound() {
        return new OrganicResult(null);
    }

    // Getters and setters (must be public to enable JSON serialization):

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
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

}
