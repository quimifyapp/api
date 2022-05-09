package com.quimify.api.inorganico;

import javax.persistence.*;
import java.util.ArrayList;

// Esta clase representa los compuestos inorgánicos.

@Entity // Es un modelo real
@Table(name = "inorganico") // En la tabla 'inorganico' de la DB
public class InorganicoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer id;

    // Necesarias:

    @Column(nullable = false)
    private String formula; // "MgH2"
    @Column(nullable = false)
    private String nombre;  // "hidruro de magnesio

    // Con valor predeterminado:

    @Column(columnDefinition = "INT default 1", nullable = false)
    private Integer busquedas = 1;
    @Column(columnDefinition = "BIT(1) default false", nullable = false)
    private Boolean premium = false;

    // Con valor predeterminado 'null':

    private String alternativo; // "dihidruro de magnesio"

    // De tipo 'String' porque no se realizan cálculos con ellos:

    private String masa;        // (g)
    private String densidad;    // (g/cm3)
    private String fusion;      // (K)
    private String ebullicion;  // (K)

    private ArrayList<String> etiquetas; // ("hidruromagnesico", "hidrogenodemagnesio", ...)

    // --------------------------------------------------------------------------------

    public void registrarBusqueda() {
        busquedas++;
    }

    // Getters y setters:

    public Integer getId() {
        return id;
    }

    public Integer getBusquedas() {
        return busquedas;
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

    public ArrayList<String> getEtiquetas() {
        return etiquetas;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setBusquedas(Integer frecuencia) {
        this.busquedas = frecuencia;
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

    public void setEtiquetas(ArrayList<String> etiquetas) {
        this.etiquetas = etiquetas;
    }

}
