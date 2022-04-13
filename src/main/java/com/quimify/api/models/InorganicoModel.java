package com.quimify.api.models;

import javax.persistence.*;

@Entity
@Table(name = "inorganico")
public class InorganicoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer id;
    private Boolean premium;

    private String formula;     // "MgH2"
    private String nombre;      // "hidruro de magnesio
    private String alternativo; // "dihidruro de magnesio"
    private String masa;        // (g)
    private String densidad;    // (g/cm3)
    private String fusion;      // (K)
    private String ebullicion;  // (K)

    public void setId(Integer id) {
        this.id = id;
    }

    public void setPremium(Boolean premium) {
        this.premium = premium;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setAlternativo(String alternativo) {
        this.alternativo = alternativo;
    }

    public void setMasa(String masa) {
        this.masa = masa;
    }

    public void setDensidad(String densidad) {
        this.densidad = densidad;
    }

    public void setFusion(String fusion) {
        this.fusion = fusion;
    }

    public void setEbullicion(String ebullicion) {
        this.ebullicion = ebullicion;
    }

    public Integer getId() {
        return id;
    }

    public Boolean getPremium() {
        return premium;
    }

    public String getFormula() {
        return formula;
    }

    public String getNombre() {
        return nombre;
    }

    public String getAlternativo() {
        return alternativo;
    }

    public String getMasa() {
        return masa;
    }

    public String getDensidad() {
        return densidad;
    }

    public String getFusion() {
        return fusion;
    }

    public String getEbullicion() {
        return ebullicion;
    }
}
