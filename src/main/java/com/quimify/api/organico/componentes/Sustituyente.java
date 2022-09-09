package com.quimify.api.organico.componentes;

import com.quimify.api.organico.Organica;

public class Sustituyente extends Organica {

    private Funciones funcion; // El tipo de sustituyente
    private int enlaces; // Número de e- que comparte con el carbono

    // Solo para radicales:
    private int carbonos;
    private boolean iso;

	// EJEMPLOS:
    /*
	cetona:     =O              →  { Id:cetona,    enlaces: 2,  carbonos: 0,  iso: false }

	propil:     -CH2-CH2-CH3    →  { Id::radical,  enlaces: 1,  carbonos: 3,  iso: false }

                           CH3
                          /
	isopentil:  -CH2-CH2-CH     →  {Id::radical,  enlaces: 1,  carbonos: 5,  iso: true  }
                          \
                           CH3
	*/

    public static final Sustituyente CH3 = new Sustituyente(1);

    // Constructores:

    public Sustituyente(int carbonos, boolean iso) {
        if(iso) {
            switch(carbonos) {
                case 0:
                    throw new IllegalArgumentException("No existen radicales con 0 carbonos.");
                case 1:
                    throw new IllegalArgumentException("No existe el \"isometil\".");
                case 2:
                    throw new IllegalArgumentException("No existe el \"isoetil\".");
                default:
                    construir(carbonos, true);
                    break;
            }
        }
        else construir(carbonos);
    }

    public Sustituyente(int carbonos) {
        construir(carbonos);
    }

    public Sustituyente(Funciones funcion) {
        switch(funcion) {
            case acido:
            case amida:
            case nitrilo:
            case aldehido:
                construir(funcion, 3);
                // Hasta aquí
                break;
            case cetona:
                construir(funcion, 2);
                break;
            case carboxil:
            case carbamoil:
            case cianuro:
            case alcohol:
            case amina:
            case eter:
            case nitro:
            case bromo:
            case cloro:
            case fluor:
            case yodo:
            case hidrogeno:
                construir(funcion, 1);
                // Hasta aquí
                break;
            case radical:
                throw new IllegalArgumentException("No existe un único sustituyente con función de radical.");
            default: // Id.alqueno, Id.alquino
                throw new IllegalArgumentException("No existen sustituyentes con función de " + funcion + ".");
        }
    }

    private void construir(Funciones funcion, int enlaces, int carbonos, boolean iso) {
        this.funcion = funcion;
        this.enlaces = enlaces;
        this.carbonos = carbonos;
        this.iso = iso;
    }

    private void construir(Funciones funcion, int enlaces) {
        construir(funcion, enlaces, 0, false);
    }

    private void construir(int carbonos, boolean iso) {
        construir(Funciones.radical, 1, carbonos, iso);
    }

    private void construir(int carbonos) {
        if(carbonos > 0)
            construir(carbonos, false);
        else throw new IllegalArgumentException("No existen radicales con 0 carbonos.");
    }

    // Consultas particulares:

    public boolean esTipo(Funciones funcion) {
        return this.funcion == funcion;
    }

    public boolean esHalogeno() {
        return Organica.esHalogeno(funcion);
    }

    @Override
    public boolean equals(Object otro) {
        boolean es_igual;

        if(otro != null && otro.getClass() == this.getClass()) {
            Sustituyente nuevo = (Sustituyente) otro;

            es_igual = funcion == Funciones.radical
                    ? carbonos == nuevo.carbonos && iso == nuevo.iso
                    : funcion == nuevo.funcion && enlaces == nuevo.enlaces;
        }
        else es_igual = false;

        return es_igual;
    }

    // Para radicales:

    public boolean esMayorRadicalQue(Sustituyente radical) {
        switch(Integer.compare(getCarbonosRectos(), radical.getCarbonosRectos())) {
            case 1: // Lo supera
                return true;
            case 0: // Lo iguala
                return iso && !radical.iso; // Pero es 'iso'
            default:
                return false;
        }
    }

    // Métodos get:

    public Funciones getFuncion() {
        return funcion;
    }


    public int getEnlaces() {
        return enlaces;
    }


    public int getCarbonos() {
        return carbonos;
    }


    public boolean getIso() {
        return iso;
    }

    // Para radicales:

    public int getCarbonosRectos() {
        return carbonos - (iso ? 1 : 0);
    }

    public Cadena getCadena() {
        Cadena cadena = new Cadena();

        if(carbonos > 0) {
            cadena.enlazarCarbono(); // (C)
            cadena.enlazar(Funciones.hidrogeno, 3); // CH3-

            int anteriores = 1; // CH3-

            if(iso) {
                cadena.enlazarCarbono(); // CH3-C≡
                cadena.enlazar(Funciones.hidrogeno); // CH3-CH=
                cadena.enlazar(CH3); // CH3-CH(CH3)-

                anteriores += 2; // CH3-CH(CH3)-
            }

            for(int i = anteriores; i < carbonos; i++) {
                cadena.enlazarCarbono(); // CH3-CH(CH3)-C≡
                cadena.enlazar(Funciones.hidrogeno, 2); // CH3-CH(CH3)-CH2-
            }

        }

        return cadena; // CH3-CH(CH3)-CH2-
    }

    // Texto:

    @Override
    public String toString() {
        StringBuilder resultado = new StringBuilder();

        switch(funcion) {
            case carboxil:
                resultado.append("C");
            case acido:
                resultado.append("OOH");
                // Hasta aquí
                break;
            case carbamoil:
                resultado.append("C");
            case amida:
                resultado.append("ONH2");
                // Hasta aquí
                break;
            case cianuro:
                resultado.append("C");
            case nitrilo:
                resultado.append("N");
                // Hasta aquí
                break;
            case aldehido:
                resultado.append("HO");
                break;
            case cetona:
                resultado.append("O");
                break;
            case alcohol:
                resultado.append("OH");
                break;
            case amina:
                resultado.append("NH2");
                break;
            case eter:
                resultado.append("-O-");
                break;
            case nitro:
                resultado.append("NO2");
                break;
            case bromo:
                resultado.append("Br");
                break;
            case cloro:
                resultado.append("Cl");
                break;
            case fluor:
                resultado.append("F");
                break;
            case yodo:
                resultado.append("I");
                break;
            case radical:
                if(iso)
                    resultado.append("CH2".repeat(Math.max(0, carbonos -  3))).append("CH(CH3)2");
                else resultado.append("CH2".repeat(Math.max(0, carbonos -  1))).append("CH3");
                break;
            case hidrogeno:
                resultado.append("H");
                break;
        }

        return resultado.toString();
    }

}
