package com.quimify.api.element;

import javax.persistence.*;

// This class represents elements of the periodic table.

@Entity
@Table(name = "element")
public class ElementModel {

    @Id
    private Integer atomicNumber; // Also known as 'Z'

    @Column(nullable = false, unique = true)
    private String symbol; // "Mg"

    @Column(nullable = false)
    private Float atomicWeight; // 24.305 (g / mol)

    // Getters and setters:

    public float getAtomicWeight() {
        return atomicWeight;
    }

}
