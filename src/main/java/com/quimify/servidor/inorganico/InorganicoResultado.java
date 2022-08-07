package com.quimify.servidor.inorganico;

// Esta clase representa las entregas al cliente de un compuesto inorgánico.

public class InorganicoResultado {

    public final static Short ENCONTRADO = 0; // OK
    public final static Short SUGERENCIA = 1; // Quizás quisiste decir...
    public final static Short NO_ENCONTRADO = 2; // Eso, o se ha producido un error

    private final Short resultado;

    // Si 'resultado' = ENCONTRADO | SUGERENCIA:

    private String formula;     // "MgH2"
    private String nombre;      // "hidruro de magnesio
    private String alternativo; // "dihidruro de magnesio"
    private String masa;        // (g)
    private String densidad;    // (g/cm3)
    private String fusion;      // (K)
    private String ebullicion;  // (K)
    private Boolean premium;    // ...

    // Si 'resultado' = SUGERENCIA:

    private String sugerencia;  // Será = 'formula', 'nombre' ó 'alternativo'

    // --------------------------------------------------------------------------------

    // Constructor:

    private void copiar(InorganicoModel inorganico) {
        this.formula = inorganico.getFormula();
        this.nombre = inorganico.getNombre();
        this.alternativo = inorganico.getAlternativo();
        this.premium = inorganico.getPremium();
        this.masa = inorganico.getMasa();
        this.densidad = inorganico.getDensidad();
        this.fusion = inorganico.getFusion();
        this.ebullicion = inorganico.getEbullicion();
    }

    public InorganicoResultado(InorganicoModel inorganico) {
        this.resultado = ENCONTRADO;
        copiar(inorganico);
    }

    public InorganicoResultado(InorganicoModel inorganico, String sugerencia) {
        this.resultado = SUGERENCIA;
        this.sugerencia = sugerencia;
        copiar(inorganico);
    }

    public InorganicoResultado() {
        this.resultado = NO_ENCONTRADO;
    }

    // Getters:

    public Short getResultado() {
        return resultado;
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

    public Boolean getPremium() {
        return premium;
    }

    public void setPremium(Boolean premium) {
        this.premium = premium;
    }

    public String getSugerencia() {
        return sugerencia;
    }

    public void setSugerencia(String sugerencia) {
        this.sugerencia = sugerencia;
    }
}
