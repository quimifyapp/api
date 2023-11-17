package com.quimify.api.inorganic;

import org.hibernate.annotations.Check;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

// This class represents inorganic compounds.

@Entity
@Table(name = "inorganic")
@Check(constraints = "(stock_name IS NOT NULL OR systematic_name IS NOT NULL OR traditional_name IS NOT NULL)")
class InorganicModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String formula; // "Ni2O3"

    private String stockName; // "óxido de níquel (III)"
    private String systematicName; // "trióxido de diníquel", "sodio"
    private String traditionalName; // "óxido niquélico"

    private String commonName; // "agua"

    // Foreign key (one to many):

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER) // Always created, deleted and fetched together
    @JoinColumn(name = "inorganic_id", nullable = false, foreignKey = @ForeignKey(name = "fk_inorganic"))
    private List<InorganicSearchTagModel> inorganicSearchTags = new ArrayList<>(); // ("aguaoxigenada", "2ho2"...)

    // Stored as text because no calculations will be performed on them:

    private String molecularMass;   // (g)
    private String density;         // (g/cm3)
    private String meltingPoint;    // (K)
    private String boilingPoint;    // (K)

    @Column(columnDefinition = "INT default 1", nullable = false)
    private Integer searches = 1;

    // Modifiers:

    void countSearch() {
        searches++;
    }

    // Queries:

    List<String> getSearchTags() {
        return inorganicSearchTags.stream().map(InorganicSearchTagModel::getNormalizedText).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        List<String> identifiers = new ArrayList<>();

        identifiers.add(Objects.toString(id));

        identifiers.add(formula);
        identifiers.add(stockName);
        identifiers.add(systematicName);
        identifiers.add(traditionalName);
        identifiers.add(commonName);

        identifiers.removeIf(Objects::isNull);

        return identifiers.toString();
    }

    // Getters and setters:

    Integer getId() {
        return id;
    }

    void setId(Integer id) {
        this.id = id;
    }

    String getFormula() {
        return formula;
    }

    void setFormula(String formula) {
        this.formula = formula;
    }

    String getStockName() {
        return stockName;
    }

    void setStockName(String stockName) {
        this.stockName = stockName;
    }

    String getSystematicName() {
        return systematicName;
    }

    void setSystematicName(String systematicName) {
        this.systematicName = systematicName;
    }

    String getTraditionalName() {
        return traditionalName;
    }

    void setTraditionalName(String traditionalName) {
        this.traditionalName = traditionalName;
    }

    String getCommonName() {
        return commonName;
    }

    void setCommonName(String otherName) {
        this.commonName = otherName;
    }

    List<InorganicSearchTagModel> getInorganicSearchTags() {
        return inorganicSearchTags;
    }

    void setInorganicSearchTags(List<InorganicSearchTagModel> inorganicSearchTags) {
        this.inorganicSearchTags = inorganicSearchTags;
    }

    Integer getSearches() {
        return searches;
    }

    void setSearches(Integer searchCount) {
        this.searches = searchCount;
    }

    String getMolecularMass() {
        return molecularMass;
    }

    void setMolecularMass(String molecularMass) {
        this.molecularMass = molecularMass;
    }

    String getDensity() {
        return density;
    }

    void setDensity(String density) {
        this.density = density;
    }

    String getMeltingPoint() {
        return meltingPoint;
    }

    void setMeltingPoint(String meltingPoint) {
        this.meltingPoint = meltingPoint;
    }

    String getBoilingPoint() {
        return boilingPoint;
    }

    void setBoilingPoint(String boilingPoint) {
        this.boilingPoint = boilingPoint;
    }

}
