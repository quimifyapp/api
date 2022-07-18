package com.quimify.api.inorganico;

// Esta clase representa las entregas al cliente de un compuesto inorgánico.

public class InorganicoResultado {

    public final static Integer NO_ENCONTRADO = 0; // Eso, o se ha producido un error
    public final static Integer ENCONTRADO = 1; // OK
    public final static Integer SUGERENCIA = 2; // Quizás quisiste decir...

    private final Integer RESULTADO;

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

    private String sugerencia;  // 'formula' | 'nombre' | 'alternativo'

    // --------------------------------------------------------------------------------

    // Constructor:

    public InorganicoResultado(InorganicoModel inorganico, Integer resultado) {
        this.RESULTADO = resultado;

        this.formula = inorganico.getFormula();
        this.nombre = inorganico.getNombre();
        this.alternativo = inorganico.getAlternativo();
        this.masa = inorganico.getMasa();
        this.densidad = inorganico.getDensidad();
        this.fusion = inorganico.getFusion();
        this.ebullicion = inorganico.getEbullicion();
    }

    public InorganicoResultado(Integer resultado) {
        this.RESULTADO = resultado;
    }

    // Getters:

    public Integer getRESULTADO() {
        return RESULTADO;
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
