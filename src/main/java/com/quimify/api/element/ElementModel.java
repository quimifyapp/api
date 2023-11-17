package com.quimify.api.element;

import javax.persistence.*;

// This class represents elements of the periodic table.

@Entity
@Table(name = "element")
public class ElementModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer atomicNumber;

    @Column(nullable = false, unique = true)
    private String symbol; // "Mg"

    @Column(nullable = false)
    private Float atomicMass;  // 24.305 (g / mol)

    // Getters and setters:

    Integer getAtomicNumber() {
        return atomicNumber;
    }

    void setAtomicNumber(Integer id) {
        this.atomicNumber = id;
    }

    String getSymbol() {
        return symbol;
    }

    void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public float getAtomicMass() {
        return atomicMass;
    }

    void setAtomicMass(float masa) {
        this.atomicMass = masa;
    }
}
