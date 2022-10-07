package com.quimify.api.organic.components;


import com.quimify.api.organic.Organic;

import java.util.*;
import java.util.stream.Collectors;

public class Carbon extends Organic {

    private final List<Substituent> substituents;
    private int enlaces_libres;

    // NUEVOS:

    public Set<FunctionalGroup> getBondedFunctions() {
        Set<FunctionalGroup> bondedFunctions = new HashSet<>();

        substituents.forEach(sustituyente -> bondedFunctions.add(sustituyente.getGroup()));

        return bondedFunctions;
    }

    // Constructor:

    public Carbon(int enlaces_previos) {
        substituents = new ArrayList<>();
        enlaces_libres = 4 - enlaces_previos;
    }

    public Carbon(FunctionalGroup functionalGroup, int veces) {
        substituents = new ArrayList<>();
        enlaces_libres = 4;
        enlazar(functionalGroup, veces);
    }

    public Carbon(Carbon otro) {
        substituents = new ArrayList<>(otro.substituents);
        enlaces_libres = otro.enlaces_libres;
    }

    // Consultas:

    public boolean contiene(FunctionalGroup functionalGroup) {
        switch(functionalGroup) {
            case alkene:
                return enlaces_libres == 1; // Como en -CO=
            case alkyne:
                return enlaces_libres == 2; // Como en -CH#
            default:
                for(Substituent substituent : substituents)
                    if(substituent.esTipo(functionalGroup))
                        return true;

                return false;
        }
    }

    public boolean contiene(Substituent substituent) {
        for(Substituent otro_substituent : substituents)
            if(otro_substituent.equals(substituent))
                return true;

        return false;
    }

    public int getCantidadDe(Substituent substituent) {
        return Collections.frequency(substituents, substituent);
    }

    public int getCantidadDe(FunctionalGroup functionalGroup) {
        int cantidad = 0;

        if(contiene(functionalGroup)) {
            if(functionalGroup != FunctionalGroup.alkene && functionalGroup != FunctionalGroup.alkyne) {
                for(Substituent substituent : substituents)
                    if(substituent.esTipo(functionalGroup))
                        cantidad += 1;
            }
            else cantidad = 1;
        }

        return cantidad;
    }

    @Override
    public boolean equals(Object otro) {
        boolean es_igual;

        if(otro != null && otro.getClass() == this.getClass()) {
            Carbon nuevo = (Carbon) otro;

            if(enlaces_libres == nuevo.enlaces_libres && substituents.size() == nuevo.substituents.size()) {
                es_igual = true;

                for(int i = 0; i < substituents.size(); i++)
                    if(!substituents.get(i).equals(nuevo.substituents.get(i))) {
                        es_igual = false;
                        break;
                    }
            }
            else es_igual = false;
        }
        else es_igual = false;

        return es_igual;
    }

    // Métodos get:

    public List<Substituent> getSustituyentesTipo(FunctionalGroup functionalGroup) {
        return substituents.stream().filter(sustituyente -> sustituyente.esTipo(functionalGroup))
                .collect(Collectors.toList());
    }

    public List<Substituent> getSubstituentsWithoutHydrogen() {
        return substituents.stream().filter(sustituyente -> !sustituyente.esTipo(FunctionalGroup.hydrogen))
                .collect(Collectors.toList());
    }

    public List<Substituent> getUnicosSustituyentes() {
        List<Substituent> unicos = new ArrayList<>();

        for(Substituent substituent : substituents)
            if(!unicos.contains(substituent))
                unicos.add(substituent);

        return unicos;
    }

    public Substituent getMayorRadical() {
        Substituent mayor_radical;

        List<Substituent> radicales = getSustituyentesTipo(FunctionalGroup.radical);
        mayor_radical = radicales.get(0); // Se asume que tiene radicales

        for(int i = 1; i < radicales.size(); i++)
            if(radicales.get(i).esMayorRadicalQue(mayor_radical))
                mayor_radical = radicales.get(i);

        return mayor_radical;
    }

    public int getEnlacesLibres() {
        return enlaces_libres;
    }

    // Métodos set:

    public void setEnlacesLibres(int enlaces_libres) {
        this.enlaces_libres = enlaces_libres;
    }

    // Texto:

    @Override
    public String toString() {
        StringBuilder resultado = new StringBuilder("C");

        // Se recogen los tipos de sustituyente:
        List<Substituent> unicos = getUnicosSustituyentes(); // Sin repetirse

        // Se ordenan según la prioridad de su función:
        Organic.ordenarPorFunciones(unicos);

        // Se escribe los hidrógenos:
        Substituent hidrogeno = new Substituent(FunctionalGroup.hydrogen);
        int cantidad = getCantidadDe(hidrogeno);
        if(cantidad > 0) {
            resultado.append(hidrogeno).append(getMolecularQuantifier(cantidad));
            unicos.remove(unicos.size() - 1); // Se borra el hidrógeno de la lista
        }

        // Se escribe el resto de sustituyentes excepto el éter:
        unicos.removeIf(sustituyente -> sustituyente.esTipo(FunctionalGroup.ether));

        if(unicos.size() == 1) { // Solo hay un tipo además del hidrógeno y éter
            Substituent unique = unicos.get(0);
            String text = unique.toString();

            if(!unique.esTipo(FunctionalGroup.aldehyde) // CH(HO)
                    && (unique.esTipo(FunctionalGroup.ketone) || unique.esHalogeno() || unique.getEnlaces() == 3))
                resultado.append(text); // "CO", "CCl", "COOH"
            else resultado.append("(").append(text).append(")"); // "CH(OH)3", "CH3(CH2CH3)"...

            resultado.append((getMolecularQuantifier(getCantidadDe(unique))));
        }
        else if(unicos.size() > 1) // Hay más de un tipo además del hidrógeno y éter
            for(Substituent substituent : unicos)
                resultado.append("(").append(substituent).append(")") // Como en "C(OH)3(Cl)", "CH2(NO2)(CH3)"...
                        .append(getMolecularQuantifier(getCantidadDe(substituent)));

        // Se escribe el éter:
        if(contiene(FunctionalGroup.ether))
            resultado.append(new Substituent(FunctionalGroup.ether));

        return resultado.toString();
    }

    // Modificadores:

    public void enlazar(Substituent substituent) {
        substituents.add(substituent);
        enlaces_libres -= substituent.getEnlaces();
    }

    public void enlazar(FunctionalGroup functionalGroup) {
        enlazar(new Substituent(functionalGroup));
    }

    public void enlazar(Substituent substituent, int veces) {
        for(int i = 0; i < veces; i++)
            enlazar(substituent);
    }

    public void enlazar(FunctionalGroup functionalGroup, int veces) {
        for(int i = 0; i < veces; i++)
            enlazar(functionalGroup);
    }

    public void eliminar(Substituent substituent) {
        substituents.remove(substituent); // No se ha eliminado su enlace
    }

    public void eliminarConEnlaces(Substituent substituent) {
        eliminar(substituent);
        enlaces_libres += substituent.getEnlaces();
    }

    public void eliminarConEnlaces(FunctionalGroup functionalGroup) {
        eliminarConEnlaces(new Substituent(functionalGroup));
    }

    public void enlazarCarbono() {
        enlaces_libres--;
    }

    public void eliminarEnlace() {
        enlaces_libres++;
    }

}
