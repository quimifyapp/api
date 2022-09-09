package com.quimify.api.organico;

public class OrganicoResultado {

    private Boolean encontrado;
    private String formula;
    private String nombre;
    private String masa;
    private String url_2d;

    // --------------------------------------------------------------------------------

    // Constructor:

    public OrganicoResultado(boolean encontrado) {
        this.encontrado = encontrado;
    }

    // Getters y setters:

    public Boolean getEncontrado() {
        return encontrado;
    }

    public void setEncontrado(Boolean encontrado) {
        this.encontrado = encontrado;
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

    public String getMasa() {
        return masa;
    }

    public void setMasa(String masa) {
        this.masa = masa;
    }

    public String getUrl_2d() {
        return url_2d;
    }

    public void setUrl_2d(String url_2d) {
        this.url_2d = url_2d;
    }

}
