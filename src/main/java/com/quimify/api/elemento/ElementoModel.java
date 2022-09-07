package com.quimify.api.elemento;

import javax.persistence.*;

// Esta clase representa los elementos químicos de la tabla periódica.

@Entity // Es un modelo real
@Table(name = "elemento") // En la tabla 'elemento' de la DB
public class ElementoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String simbolo; // "Mg"

    @Column(nullable = false)
    private Float masa;  // 24.305 (g / mol)

    // --------------------------------------------------------------------------------

    // Getters y setters:

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    public float getMasa() {
        return masa;
    }

    public void setMasa(float masa) {
        this.masa = masa;
    }
}
