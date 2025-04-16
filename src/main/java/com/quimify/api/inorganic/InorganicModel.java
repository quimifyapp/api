package com.quimify.api.inorganic;

import org.hibernate.annotations.Check;
import javax.persistence.*;
import java.util.*;

@MappedSuperclass
@Check(constraints = "(stock_name IS NOT NULL OR systematic_name IS NOT NULL OR traditional_name IS NOT NULL)")
public abstract class InorganicModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @Column(nullable = false)
    protected String formula; // "Ni2O3"

    @Column(unique = true)
    protected String stockName; // "óxido de níquel (III)"
    @Column(unique = true)
    protected String systematicName; // "trióxido de diníquel", "sodio"
    @Column(unique = true)
    protected String traditionalName; // "óxido niquélico"

    protected String commonName; // "agua"

    protected String molecularMass;   // (g)
    protected String density;         // (g/cm3)
    protected String meltingPoint;    // (K)
    protected String boilingPoint;    // (K)

    @Column(columnDefinition = "INT default 1", nullable = false)
    protected Integer searches = 1;

    // Protected no-args constructor for JPA
    protected InorganicModel() { }

    // Modifiers:
    protected void countSearch() {
        searches++;
    }

    // Queries:
    public abstract List<String> getSearchTags();

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
    public Integer getId() {
        return id;
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
