package com.quimify.api.organico.componentes;


import com.quimify.api.organico.Organica;

import java.util.*;
import java.util.stream.Collectors;

public class Carbono extends Organica {

    private final List<Sustituyente> sustituyentes;
    private int enlaces_libres;

    // Constructor:

    public Carbono(int enlaces_previos) {
        sustituyentes = new ArrayList<>();
        enlaces_libres = 4 - enlaces_previos;
    }

    public Carbono(Funciones funcion, int veces) {
        sustituyentes = new ArrayList<>();
        enlaces_libres = 4;
        enlazar(funcion, veces);
    }

    public Carbono(Carbono otro) {
        sustituyentes = new ArrayList<>(otro.sustituyentes);
        enlaces_libres = otro.enlaces_libres;
    }

    // Consultas:

    public boolean contiene(Funciones funcion) {
        switch(funcion) {
            case alqueno:
                return enlaces_libres == 1; // Como en -CO=
            case alquino:
                return enlaces_libres == 2; // Como en -CH#
            default:
                for(Sustituyente sustituyente : sustituyentes)
                    if(sustituyente.esTipo(funcion))
                        return true;

                return false;
        }
    }

    public boolean contiene(Sustituyente sustituyente) {
        for(Sustituyente otro_sustituyente : sustituyentes)
            if(otro_sustituyente.equals(sustituyente))
                return true;

        return false;
    }

    public int getCantidadDe(Sustituyente sustituyente) {
        return Collections.frequency(sustituyentes, sustituyente);
    }

    public int getCantidadDe(Funciones funcion) {
        int cantidad = 0;

        if(contiene(funcion)) {
            if(funcion != Funciones.alqueno && funcion != Funciones.alquino) {
                for(Sustituyente sustituyente : sustituyentes)
                    if(sustituyente.esTipo(funcion))
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
            Carbono nuevo = (Carbono) otro;

            if(enlaces_libres == nuevo.enlaces_libres && sustituyentes.size() == nuevo.sustituyentes.size()) {
                es_igual = true;

                for(int i = 0; i < sustituyentes.size(); i++)
                    if(!sustituyentes.get(i).equals(nuevo.sustituyentes.get(i))) {
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

    public List<Sustituyente> getSustituyentesTipo(Funciones funcion) {
        return sustituyentes.stream().filter(sustituyente -> sustituyente.esTipo(funcion))
                .collect(Collectors.toList());
    }

    public List<Sustituyente> getSustituyentesSinHidrogeno() {
        return sustituyentes.stream().filter(sustituyente -> !sustituyente.esTipo(Funciones.hidrogeno))
                .collect(Collectors.toList());
    }

    public List<Sustituyente> getUnicosSustituyentes() {
        List<Sustituyente> unicos = new ArrayList<>();

        for(Sustituyente sustituyente : sustituyentes)
            if(!unicos.contains(sustituyente))
                unicos.add(sustituyente);

        return unicos;
    }

    public List<Sustituyente> getSustituyentes() {
        return sustituyentes;
    }

    public Sustituyente getMayorRadical() {
        Sustituyente mayor_radical;

        List<Sustituyente> radicales = getSustituyentesTipo(Funciones.radical);
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
        List<Sustituyente> unicos = getUnicosSustituyentes(); // Sin repetirse

        // Se ordenan según la prioridad de su función:
        Organica.ordenarPorFunciones(unicos);

        // Se escribe los hidrógenos:
        Sustituyente hidrogeno = new Sustituyente(Funciones.hidrogeno);
        int cantidad = getCantidadDe(hidrogeno);
        if(cantidad > 0) {
            resultado.append(hidrogeno).append(cuantificadorMolecular(cantidad));
            unicos.remove(unicos.size() - 1); // Se borra el hidrógeno de la lista
        }

        // Se escribe el resto de sustituyentes excepto el éter:
        unicos.removeIf(sustituyente -> sustituyente.esTipo(Funciones.eter));

        if(unicos.size() == 1) { // Solo hay un tipo además del hidrógeno y éter
            Sustituyente unico = unicos.get(0);
            String texto = unico.toString();

            if(texto.length() == 1 || unico.esHalogeno() || unico.getEnlaces() == 3)
                resultado.append(texto); // Como en "CN", "CCl", "COOH", "C(O)(NH2)", "CHO"...
            else resultado.append("(").append(texto).append(")"); // Como en "CH(OH)3", "CH3(CH2CH3)"...

            resultado.append((cuantificadorMolecular(getCantidadDe(unico))));
        }
        else if(unicos.size() > 1) // Hay más de un tipo además del hidrógeno y éter
            for(Sustituyente sustituyente : unicos)
                resultado.append("(").append(sustituyente).append(")") // Como en "C(OH)3(Cl)", "CH2(NO2)(CH3)"...
                        .append(cuantificadorMolecular(getCantidadDe(sustituyente)));

        // Se escribe el éter:
        if(contiene(Funciones.eter))
            resultado.append(new Sustituyente(Funciones.eter));

        return resultado.toString();
    }

    // Modificadores:

    public void enlazar(Sustituyente sustituyente) {
        sustituyentes.add(sustituyente);
        enlaces_libres -= sustituyente.getEnlaces();
    }

    public void enlazar(Funciones funcion) {
        enlazar(new Sustituyente(funcion));
    }

    public void enlazar(Sustituyente sustituyente, int veces) {
        for(int i = 0; i < veces; i++)
            enlazar(sustituyente);
    }

    public void enlazar(Funciones funcion, int veces) {
        for(int i = 0; i < veces; i++)
            enlazar(funcion);
    }

    public void eliminar(Sustituyente sustituyente) {
        sustituyentes.remove(sustituyente); // No se ha eliminado su enlace
    }

    public void eliminarConEnlaces(Sustituyente sustituyente) {
        eliminar(sustituyente);
        enlaces_libres += sustituyente.getEnlaces();
    }

    public void eliminarConEnlaces(Funciones funcion) {
        eliminarConEnlaces(new Sustituyente(funcion));
    }

    public void enlazarCarbono() {
        enlaces_libres--;
    }

    public void eliminarEnlace() {
        enlaces_libres++;
    }

}
