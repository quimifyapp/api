package com.quimify.servidor;

public class ContextoCliente {

    // Medios:
    public static final Short TECLADO = 0; // Inorgánicos
    public static final Short CAMARA = 1; // Inorgánicos
    public static final Short GALERIA = 2; // Inorgánicos

    // Campos:

    private Short medio;
    private Boolean premium;

    // --------------------------------------------------------------------------------

    // Constructor:

    public ContextoCliente(Short medio, Boolean premium) {
        setMedio(medio);
        setPremium(premium);
    }

    public ContextoCliente(Short medio) {
        setMedio(medio);
        setPremium(false);
    }

    public ContextoCliente(Boolean premium) {
        setMedio(TECLADO);
        setPremium(premium);
    }

    // Getters y setters:

    public Short getMedio() {
        return medio;
    }

    public void setMedio(Short medio) {
        this.medio = medio;
    }

    public Boolean getPremium() {
        return premium;
    }

    public void setPremium(Boolean premium) {
        this.premium = premium;
    }

}
