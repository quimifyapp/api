package com.quimify.api.organic.components;

import com.quimify.api.organic.Organic;

public class Substituent extends Organic {

    private FunctionalGroup functionalGroup; // El tipo de sustituyente
    private int bondCount; // Número de e- que comparte con el carbono

    // Solo para radicales:
    private int carbonCount;
    private boolean isIso;

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

    public static final Substituent CH3 = new Substituent(1);

    // Constructores:

    public Substituent(int carbonCount, boolean isIso) {
        if(isIso) {
            switch(carbonCount) {
                case 0:
                    throw new IllegalArgumentException("No existen radicales con 0 carbonos.");
                case 1:
                    throw new IllegalArgumentException("No existe el \"isometil\".");
                case 2:
                    throw new IllegalArgumentException("No existe el \"isoetil\".");
                default:
                    construir(carbonCount, true);
                    break;
            }
        }
        else construir(carbonCount);
    }

    public Substituent(int carbonCount) {
        construir(carbonCount);
    }

    public Substituent(FunctionalGroup functionalGroup) {
        switch(functionalGroup) {
            case acid:
            case amide:
            case nitrile:
            case aldehyde:
                construir(functionalGroup, 3);
                // Hasta aquí
                break;
            case ketone:
                construir(functionalGroup, 2);
                break;
            case carboxyl:
            case carbamoyl:
            case cyanide:
            case alcohol:
            case amine:
            case ether:
            case nitro:
            case bromine:
            case chlorine:
            case fluorine:
            case iodine:
            case hydrogen:
                construir(functionalGroup, 1);
                // Hasta aquí
                break;
            case radical:
                throw new IllegalArgumentException("No existe un único sustituyente con función de radical.");
            default: // Id.alqueno, Id.alquino
                throw new IllegalArgumentException("No existen sustituyentes con función de [" + functionalGroup + "].");
        }
    }

    private void construir(FunctionalGroup functionalGroup, int enlaces, int carbonos, boolean iso) {
        this.functionalGroup = functionalGroup;
        this.bondCount = enlaces;
        this.carbonCount = carbonos;
        this.isIso = iso;
    }

    private void construir(FunctionalGroup functionalGroup, int enlaces) {
        construir(functionalGroup, enlaces, 0, false);
    }

    private void construir(int carbonos, boolean iso) {
        construir(FunctionalGroup.radical, 1, carbonos, iso);
    }

    private void construir(int carbonos) {
        if(carbonos > 0)
            construir(carbonos, false);
        else throw new IllegalArgumentException("No existen radicales con 0 carbonos.");
    }

    // Consultas particulares:

    public boolean esTipo(FunctionalGroup functionalGroup) {
        return this.functionalGroup == functionalGroup;
    }

    public boolean esHalogeno() {
        return Organic.esHalogeno(functionalGroup);
    }

    @Override
    public boolean equals(Object otro) {
        boolean es_igual;

        if(otro != null && otro.getClass() == this.getClass()) {
            Substituent nuevo = (Substituent) otro;

            es_igual = functionalGroup == FunctionalGroup.radical
                    ? carbonCount == nuevo.carbonCount && isIso == nuevo.isIso
                    : functionalGroup == nuevo.functionalGroup && bondCount == nuevo.bondCount;
        }
        else es_igual = false;

        return es_igual;
    }

    // Para radicales:

    public boolean esMayorRadicalQue(Substituent radical) {
        switch(Integer.compare(getCarbonosRectos(), radical.getCarbonosRectos())) {
            case 1: // Lo supera
                return true;
            case 0: // Lo iguala
                return isIso && !radical.isIso; // Pero es 'iso'
            default:
                return false;
        }
    }

    // Métodos get:

    public FunctionalGroup getGroup() {
        return functionalGroup;
    }


    public int getEnlaces() {
        return bondCount;
    }


    public int getCarbonCount() {
        return carbonCount;
    }


    public boolean getIso() {
        return isIso;
    }

    // Para radicales:

    public int getCarbonosRectos() {
        return carbonCount - (isIso ? 1 : 0);
    }

    public Chain getCadena() {
        Chain chain = new Chain();

        if(carbonCount > 0) {
            chain.enlazarCarbono(); // (C)
            chain.enlazar(FunctionalGroup.hydrogen, 3); // CH3-

            int anteriores = 1; // CH3-

            if(isIso) {
                chain.enlazarCarbono(); // CH3-C≡
                chain.enlazar(FunctionalGroup.hydrogen); // CH3-CH=
                chain.enlazar(CH3); // CH3-CH(CH3)-

                anteriores += 2; // CH3-CH(CH3)-
            }

            for(int i = anteriores; i < carbonCount; i++) {
                chain.enlazarCarbono(); // CH3-CH(CH3)-C≡
                chain.enlazar(FunctionalGroup.hydrogen, 2); // CH3-CH(CH3)-CH2-
            }

        }

        return chain; // CH3-CH(CH3)-CH2-
    }

    // Texto:

    @Override
    public String toString() {
        StringBuilder resultado = new StringBuilder();

        switch(functionalGroup) {
            case carboxyl:
                resultado.append("C");
            case acid:
                resultado.append("OOH");
                // Hasta aquí
                break;
            case carbamoyl:
                resultado.append("C");
            case amide:
                resultado.append("ONH2");
                // Hasta aquí
                break;
            case cyanide:
                resultado.append("C");
            case nitrile:
                resultado.append("N");
                // Hasta aquí
                break;
            case aldehyde:
                resultado.append("HO");
                break;
            case ketone:
                resultado.append("O");
                break;
            case alcohol:
                resultado.append("OH");
                break;
            case amine:
                resultado.append("NH2");
                break;
            case ether:
                resultado.append("-O-");
                break;
            case nitro:
                resultado.append("NO2");
                break;
            case bromine:
                resultado.append("Br");
                break;
            case chlorine:
                resultado.append("Cl");
                break;
            case fluorine:
                resultado.append("F");
                break;
            case iodine:
                resultado.append("I");
                break;
            case radical:
                if(isIso)
                    resultado.append("CH2".repeat(Math.max(0, carbonCount -  3))).append("CH(CH3)2");
                else resultado.append("CH2".repeat(Math.max(0, carbonCount -  1))).append("CH3");
                break;
            case hydrogen:
                resultado.append("H");
                break;
        }

        return resultado.toString();
    }

}
