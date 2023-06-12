package com.quimify.api.inorganic;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

// This class represents inorganic compounds.

@Entity
@Table(name = "inorganic")
class InorganicModel {

    // Non-nullable:

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String formula; // "Ni2O3"

    // With 'null' default value:

    private String stockName; // "óxido de níquel (III)"
    private String systematicName; // "trióxido de diníquel", "sodio"
    private String traditionalName; // "óxido niquélico"

    private String commonName; // "agua"

    // Foreign key (one to many):

    @JoinColumn(name = "inorganic_id")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER) // Always created, deleted and fetched together
    private Set<InorganicSearchTagModel> inorganicSearchTags = new HashSet<>(); // "aguaoxigenada"

    // With default value:

    @Column(columnDefinition = "INT default 1", nullable = false)
    private Integer searchCount = 1;

    // Typed 'String' because no calculations will be performed:

    private String molecularMass;   // (g)
    private String density;         // (g/cm3)
    private String meltingPoint;    // (K)
    private String boilingPoint;    // (K)

    // Modifiers:

    @Transactional
    void addSearchTag(String text) {
        inorganicSearchTags.add(new InorganicSearchTagModel(text));
    }

    // TODO unused. Be used!
    @Transactional // TODO transactional here?
    void countSearch() {
        searchCount++; // TODO rename to 'searches'?
    }

    // Queries:

    Set<String> getSearchTags() {
        return inorganicSearchTags.stream().map(InorganicSearchTagModel::getNormalizedTag).collect(Collectors.toSet());
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

    Set<InorganicSearchTagModel> getInorganicSearchTags() {
        return inorganicSearchTags;
    }

    void setInorganicSearchTags(Set<InorganicSearchTagModel> inorganicSearchTags) {
        this.inorganicSearchTags = inorganicSearchTags;
    }

    Integer getSearchCount() {
        return searchCount;
    }

    void setSearchCount(Integer searchCount) {
        this.searchCount = searchCount;
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
