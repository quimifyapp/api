package com.quimify.api.organic.bridges.opsin;

import uk.ac.cam.ch.wwmm.opsin.*;

import java.util.Optional;

public class Opsin {

    protected static final es.opsin.NameToStructure opsin_es = es.opsin.NameToStructure.getInstance();
    protected static final NameToStructure opsin_en = NameToStructure.getInstance();

    public static Optional<OpsinResult> procesarNombreES(String nombre) {
        Optional<OpsinResult> resultado;

        // La sintaxis que permite OPSIN es inglesa, donde "cloruro de sodio" es como "sodio cloruro":
        if(nombre.contains(" de ")) {
            String[] palabras = nombre.split(" de "); // Ej.: {"cloruro", "sodio"}
            nombre = palabras[1] + " " + palabras[0]; // Ej.: "sodio cloruro"
        }

        // Nuestra adaptación al español de la librería OPSIN rechaza el prefijo "ácido", por eso se elimina:
        nombre = nombre.replaceFirst("ácido|acido", "");

        // La librería OPSIN rechaza el prefijo "di" de los éteres simétricos en algunos casos:
        nombre = corregirEter(nombre);

        // Se procesa:
        es.opsin.OpsinResult opsin_result = opsin_es.parseChemicalName(nombre);

        // Se convierte a la clase propia 'OpsinResultado':
        if(opsin_result.getStatus() == es.opsin.OpsinResult.OPSIN_RESULT_STATUS.SUCCESS)
            resultado = Optional.of(new OpsinResult(opsin_result));
        else resultado = Optional.empty();

        return resultado;
    }
    public static Optional<OpsinResult> procesarNombreEN(String nombre) {
        Optional<OpsinResult> resultado;

        // La librería OPSIN rechaza el prefijo "di" de los éteres simétricos en algunos casos:
        nombre = corregirEter(nombre);

        // Se procesa:
        uk.ac.cam.ch.wwmm.opsin.OpsinResult opsin_result = opsin_en.parseChemicalName(nombre);

        // Se convierte a la clase propia 'OpsinResultado':
        if(opsin_result.getStatus() == uk.ac.cam.ch.wwmm.opsin.OpsinResult.OPSIN_RESULT_STATUS.SUCCESS)
            resultado = Optional.of(new OpsinResult(opsin_result));
        else resultado = Optional.empty();

        return resultado;
    }

    private static String corregirEter(String nombre) {
        if(nombre.contains("éter") || nombre.contains("eter") || nombre.contains("ether")) {
            String arreglado = nombre.replaceFirst("di", "");

            // Puede que el "di" no sea porque es simétrico, sino que sea el cuantificador de un sustituyente:
            if(arreglado.trim().split("\\s+").length == 2) // Como "etil éter"
                nombre = arreglado;
        }

        return nombre;
    }

}
