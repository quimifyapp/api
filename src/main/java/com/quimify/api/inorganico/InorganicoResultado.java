package com.quimify.api.inorganico;

// Esta clase representa las entregas al cliente de un compuesto inorgánico.

public class InorganicoResultado {

    public final static Integer NO_ENCONTRADO = 0; // Eso, o se ha producido un error
    public final static Integer ENCONTRADO = 1; // OK
    public final static Integer NO_PREMIUM = 2; // Compuesto premium, usuario no-premium

    private final Integer resultado;

    private String formula;       // "MgH2"
    private String nombre;        // "hidruro de magnesio
    private String alternativo;   // "dihidruro de magnesio"
    private String masa;          // (g)
    private String densidad;      // (g/cm3)
    private String fusion;        // (K)
    private String ebullicion;    // (K)

    private Boolean premium; // Para futura "insignia"

    // --------------------------------------------------------------------------------

    public InorganicoResultado(InorganicoModel inorganico) {
        resultado = ENCONTRADO;

        this.formula = inorganico.getFormula();
        this.nombre = inorganico.getNombre();
        this.alternativo = inorganico.getAlternativo();
        this.masa = inorganico.getMasa();
        this.densidad = inorganico.getDensidad();
        this.fusion = inorganico.getFusion();
        this.ebullicion = inorganico.getEbullicion();
    }

    public InorganicoResultado(Integer resultado) {
        this.resultado = resultado;
    }

    // Getters:

    public Integer getResultado(){
        return resultado;
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
