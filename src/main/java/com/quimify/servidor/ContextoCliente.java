package com.quimify.servidor;

public class ContextoCliente {

    // Pantallas:
    public static final Short TECLADO = 0; // Inorgánicos
    public static final Short CAMARA = 1; // Inorgánicos
    public static final Short GALERIA = 2; // Inorgánicos

    // Campos:

    private Short pantalla;
    private Boolean premium;

    // --------------------------------------------------------------------------------

    // Constructor:

    public ContextoCliente(Short pantalla, Boolean premium) {
        setPantalla(pantalla);
        setPremium(premium);
    }

    public ContextoCliente(Short pantalla) {
        setPantalla(pantalla);
        setPremium(false);
    }

    public ContextoCliente(Boolean premium) {
        setPantalla(TECLADO);
        setPremium(premium);
    }

    // Getters y setters:

    public Short getPantalla() {
        return pantalla;
    }

    public void setPantalla(Short pantalla) {
        this.pantalla = pantalla;
    }

    public Boolean getPremium() {
        return premium;
    }

    public void setPremium(Boolean premium) {
        this.premium = premium;
    }

}
