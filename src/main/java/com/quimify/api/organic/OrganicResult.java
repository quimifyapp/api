package com.quimify.api.organic;

// This POJO class represents responses of organic compounds to the client.

class OrganicResult {

    private Boolean present;

    // If present is true:

    private String name;
    private String structure;
    private Float molecularMass;
    private String url2D;

    // Constants:

    protected static final OrganicResult notFound = new OrganicResult();

    // Constructors:

    public OrganicResult(String name, String structure, Float molecularMass, String url2D) {
        this.present = true;
        this.name = name;
        this.structure = structure;
        this.molecularMass = molecularMass;
        this.url2D = url2D;
    }

    private OrganicResult() {
        this.present = false;
    }

    // Getters and setters:

    public Boolean getPresent() { // TODO rename isPresent
        return present;
    }

    public void setPresent(Boolean present) {
        this.present = present;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
