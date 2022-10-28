package com.quimify.api.element;

import javax.persistence.*;

// Esta clase representa los elementos químicos de la tabla periódica.

@Entity // Es un modelo real
@Table(name = "element")
public // En la tabla 'elemento' de la DB
class ElementModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String symbol; // "Mg"

    @Column(nullable = false)
    private Float molecularMass;  // 24.305 (g / mol)

    // --------------------------------------------------------------------------------

    // Getters y setters:

    protected Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        this.id = id;
    }

    protected String getSymbol() {
        return symbol;
    }

    protected void setSymbol(String simbolo) {
        this.symbol = simbolo;
    }

    public float getMolecularMass() {
        return molecularMass;
    }

    protected void setMolecularMass(float masa) {
        this.molecularMass = masa;
    }
}
