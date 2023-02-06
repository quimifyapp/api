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

    // Queries:

    protected Set<String> getSearchTags() {
        return inorganicSearchTags.stream().map(InorganicSearchTagModel::getNormalizedTag).collect(Collectors.toSet());
    }

    // Modifiers:

    @Transactional
    protected void addSearchTag(String text) {
        inorganicSearchTags.add(new InorganicSearchTagModel(text));
    }

    @Transactional
    protected void countSearch() {
        searchCount++;
    }


    @Override
    public String toString() {
        List<String> words = new ArrayList<>();

        words.add(Objects.toString(id));

        words.add(formula);
        words.add(stockName);
        words.add(systematicName);
        words.add(traditionalName);
        words.add(commonName);

        words.removeIf(Objects::isNull);

        return words.toString();
    }

    // Getters and setters:

    protected Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        this.id = id;
    }

    protected String getFormula() {
        return formula;
    }

    protected void setFormula(String formula) {
        this.formula = formula;
    }

    protected String getStockName() {
        return stockName;
    }

    protected void setStockName(String stockName) {
        this.stockName = stockName;
    }

    protected String getSystematicName() {
        return systematicName;
    }

    protected void setSystematicName(String systematicName) {
        this.systematicName = systematicName;
    }

    protected String getTraditionalName() {
        return traditionalName;
    }

    protected void setTraditionalName(String traditionalName) {
        this.traditionalName = traditionalName;
    }

    protected String getCommonName() {
        return commonName;
    }

    protected void setCommonName(String otherName) {
        this.commonName = otherName;
    }

    protected Set<InorganicSearchTagModel> getInorganicSearchTags() {
        return inorganicSearchTags;
    }

    protected void setInorganicSearchTags(Set<InorganicSearchTagModel> inorganicSearchTags) {
        this.inorganicSearchTags = inorganicSearchTags;
    }

    protected Integer getSearchCount() {
        return searchCount;
    }

    protected void setSearchCount(Integer searchCount) {
        this.searchCount = searchCount;
    }

    protected String getMolecularMass() {
        return molecularMass;
    }

    protected void setMolecularMass(String molecularMass) {
        this.molecularMass = molecularMass;
    }

    protected String getDensity() {
        return density;
    }

    protected void setDensity(String density) {
        this.density = density;
    }

    protected String getMeltingPoint() {
        return meltingPoint;
    }

    protected void setMeltingPoint(String meltingPoint) {
        this.meltingPoint = meltingPoint;
    }

    protected String getBoilingPoint() {
        return boilingPoint;
    }

    protected void setBoilingPoint(String boilingPoint) {
        this.boilingPoint = boilingPoint;
    }

}
