package com.quimify.api.organic;

public class OrganicResult {

    private Boolean encontrado;
    private String nombre;
    private String formula;
    private String masa;
    private String url_2d;

    // Si 'encontrado' = false:

    private Boolean es_inorganico_sugerencia;

    // --------------------------------------------------------------------------------

    // Constructor:

    public OrganicResult(boolean encontrado) {
        this.encontrado = encontrado;
        this.es_inorganico_sugerencia = false;
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

    public Boolean getEs_inorganico_sugerencia() {
        return es_inorganico_sugerencia;
    }

    public void setEs_inorganico_sugerencia(Boolean es_inorganico_sugerencia) {
        this.es_inorganico_sugerencia = es_inorganico_sugerencia;
    }

}
