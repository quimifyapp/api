package com.quimify.api.inorganic;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

// Esta clase representa los compuestos inorgánicos.

@Entity // Es un modelo real
@Table(name = "inorganic") // En la tabla 'inorganico' de la DB
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
    private String systematicName; // "trióxido de diníquel"
    private String traditionalName; // "óxido niquélico"

    private String otherName; // "potasio"

    // Foreign key (one to many):

    @OneToMany(cascade = CascadeType.ALL) // Se crean de la mano y se borran de la mano
    @JoinColumn(name = "inorganic_id") // Cada etiqueta lleva asociado el inorgánico al que pertenece
    private Set<InorganicSearchTagModel> searchTags = new HashSet<>(); // "hidruromagnesico" | "aguaoxigenada" | ...

    // With default value:

    @Column(columnDefinition = "INT default 1", nullable = false)
    private Integer searchCount = 1;

    // Typed 'String' because no calculations will be performed:

    private String molecularMass;    // (g)
    private String density;         // (g/cm3)
    private String meltingPoint;    // (K)
    private String boilingPoint;    // (K)

    // Modifiers:

    @Transactional
    protected void countSearch() {
        searchCount++;
    }

    protected void addSearchTagOf(String tag) {
        searchTags.add(new InorganicSearchTagModel(tag));
    }

    // Queries:

    protected Set<String> getSearchTagsAsStrings() {
        return searchTags.stream().map(InorganicSearchTagModel::getNormalizedTag).collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        List<String> words = new ArrayList<>();

        words.add(id.toString());

        words.add(formula);
        words.add(stockName);
        words.add(systematicName);
        words.add(traditionalName);
        words.add(otherName);

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

    protected String getOtherName() {
        return otherName;
    }

    protected void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    protected Set<InorganicSearchTagModel> getSearchTags() {
        return searchTags;
    }

    protected void setSearchTags(Set<InorganicSearchTagModel> searchTags) {
        this.searchTags = searchTags;
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
