package com.quimify.api.element;

import javax.persistence.*;

// Esta clase representa los elementos químicos de la tabla periódica.
// TODO translate comments

@Entity // Es un modelo real
@Table(name = "element")
public class ElementModel {

    @Id
    private Integer id;

    @Column(nullable = false)
    private String symbol; // "Mg"

    @Column(nullable = false)
    private Float molecularMass;  // 24.305 (g / mol)

    // Getters y setters:

    Integer getId() {
        return id;
    }

    void setId(Integer id) {
        this.id = id;
    }

    String getSymbol() {
        return symbol;
    }

    void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public float getMolecularMass() {
        return molecularMass;
    }

    void setMolecularMass(float masa) {
        this.molecularMass = masa;
    }
}
