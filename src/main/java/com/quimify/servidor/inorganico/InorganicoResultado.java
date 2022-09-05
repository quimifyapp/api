package com.quimify.servidor.inorganico;

// Esta clase representa las entregas al cliente de un compuesto inorgánico.

public class InorganicoResultado {

    private Boolean encontrado;
    private Boolean premium;

    // Si 'encontrado' = true:

    private String formula;     // "MgH2"
    private String nombre;      // "hidruro de magnesio
    private String alternativo; // "dihidruro de magnesio"
    private String masa;        // (g)
    private String densidad;    // (g/cm³)
    private String fusion;      // (K)
    private String ebullicion;  // (K)

    // Si 'encontrado' = false:

    private String sugerencia;  // Será = 'formula', 'nombre' ó 'alternativo'

    private Boolean es_organico_sugerencia;

    // --------------------------------------------------------------------------------

    // Constructores:

    public InorganicoResultado(InorganicoModel inorganico) {
        this.encontrado = true;

        copiar(inorganico);
    }

    public InorganicoResultado() {
        this.encontrado = false;
    }

    private void copiar(InorganicoModel inorganico) {
        this.premium = inorganico.getPremium();
        this.formula = inorganico.getFormula();
        this.nombre = inorganico.getNombre();
        this.alternativo = inorganico.getAlternativo();
        this.masa = inorganico.getMasa();
        this.densidad = inorganico.getDensidad();
        this.fusion = inorganico.getFusion();
        this.ebullicion = inorganico.getEbullicion();
    }

    // Getters y setters:

    public Boolean getEncontrado() {
        return encontrado;
    }

    public void setEncontrado(Boolean encontrado) {
        this.encontrado = encontrado;
    }

    public Boolean getPremium() {
        return premium;
    }

    public void setPremium(Boolean premium) {
        this.premium = premium;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAlternativo() {
        return alternativo;
    }

    public void setAlternativo(String alternativo) {
        this.alternativo = alternativo;
    }

    public String getMasa() {
        return masa;
    }

    public void setMasa(String masa) {
        this.masa = masa;
    }

    public String getDensidad() {
        return densidad;
    }

    public void setDensidad(String densidad) {
        this.densidad = densidad;
    }

    public String getFusion() {
        return fusion;
    }

    public void setFusion(String fusion) {
        this.fusion = fusion;
    }

    public String getEbullicion() {
        return ebullicion;
    }

    public void setEbullicion(String ebullicion) {
        this.ebullicion = ebullicion;
    }

    public String getSugerencia() {
        return sugerencia;
    }

    public void setSugerencia(String sugerencia) {
        this.sugerencia = sugerencia;
    }

    public Boolean getEs_organico_sugerencia() {
        return es_organico_sugerencia;
    }

    public void setEs_organico_sugerencia(Boolean es_organico_sugerencia) {
        this.es_organico_sugerencia = es_organico_sugerencia;
    }

}
