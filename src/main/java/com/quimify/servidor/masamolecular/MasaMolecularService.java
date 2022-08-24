package com.quimify.servidor.masamolecular;

import com.quimify.servidor.ContextoCliente;
import com.quimify.servidor.elemento.ElementoModel;
import com.quimify.servidor.elemento.ElementoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

// Esta clase procesa las masas moleculares.

@Service
public class MasaMolecularService {

    @Autowired
    ElementoService elementoService; // Procesos de los elementos

    // INTERNOS ----------------------------------------------------------------------

    private Optional<Float> getMasaElemento(String simbolo) {
        return elementoService.buscarSimbolo(simbolo).map(ElementoModel::getMasa);
    }

    private void agregarAlMapa(String key, Integer value, Map<String, Integer> mapa) {
        Integer encontrado = mapa.get(key);

        if(encontrado != null)
            mapa.replace(key, encontrado + value); // Ya estaba, se suma
        else mapa.put(key, value); // Nuevo
    }

    private MasaMolecularResultado calcularMasaMolecular(String formula) {
        MasaMolecularResultado resultado;

        float masa_molecular = 0;

        // Procesa las fórmulas anidadas entre paréntesis de forma recursiva:

        Map<String, Integer> anidada_a_moles = new HashMap<>();

        int balance = 0;

        for(int i = 0, parentesis = 0; i < formula.length(); i++) {
            if(formula.charAt(i) == ')') {
                balance -= 1;

                if(balance == 0) { // Implica que hay '(' antes
                    StringBuilder anidada = new StringBuilder(formula.substring(parentesis + 1, i));

                    // Se procesan los cuantificadores:

                    i += 1; // Lo siguiente tras el último paréntesis

                    int moles;
                    if(i < formula.length()) {
                        String digitos = formula.substring(i).replaceAll("(\\d+).+", "$1");
                        moles = digitos.length() > 0 ? Integer.parseInt(digitos) : 1;
                    }
                    else moles = 1;

                    formula = formula.replace("(" + anidada + ")" + moles, ""); // Se borra la anidada

                    i = parentesis; // Se vuelve a comenzar por el primer paréntesis

                    agregarAlMapa(anidada.toString(), moles, anidada_a_moles); // Se registra la fórmula anidada
                }
                else if(balance < 0)
                    return new MasaMolecularResultado("Los paréntesis no están balanceados.");
            }
            else if(formula.charAt(i) == '(') {
                if(balance == 0)
                    parentesis = i;

                balance += 1;
            }
        }

        // Se suman las masas de las fórmulas anidadas:

        Map<String, Integer> elemento_a_moles = new HashMap<>();

        for(Map.Entry<String, Integer> anidada : anidada_a_moles.entrySet()) {
            MasaMolecularResultado masa_molecular_anidada = calcularMasaMolecular(anidada.getKey());

            for(Map.Entry<String, Integer> elemento : masa_molecular_anidada.getElemento_a_moles().entrySet())
                agregarAlMapa(elemento.getKey(), elemento.getValue() * anidada.getValue(), elemento_a_moles);
        }

        // Se comprueba que no quede ningún paréntesis suelto:

        if(balance == 0 && !(formula.contains("(") || formula.contains(")"))) {
            // Procesa lo que no va entre paréntesis:

            String[] partes = formula.split("(?=[A-Z])"); // "Aa11Bb22" -> ("Aa11", "Bb22")

            // Se registran los elementos y sus moles:

            for(String parte : partes) {
                String simbolo = parte.replaceAll("\\d", "");
                String digitos = parte.replaceAll("[A-Za-z]", "");
                int moles = digitos.length() > 0 ? Integer.parseInt(digitos) : 1;

                agregarAlMapa(simbolo, moles, elemento_a_moles); // Registra el elemento
            }

            // Se calculan los gramos de cada elemento y el total es la masa molecular:

            Map<String, Float> elemento_a_gramos = new HashMap<>();

            for(Map.Entry<String, Integer> elemento : elemento_a_moles.entrySet()) {
                String simbolo = elemento.getKey();

                Optional<Float> masa_elemento = getMasaElemento(simbolo);

                if(masa_elemento.isPresent()) {
                    float gramos = elemento.getValue() * masa_elemento.get();
                    elemento_a_gramos.put(simbolo, gramos);

                    masa_molecular += gramos;
                }
                else return new MasaMolecularResultado("No se reconoce el elemento \"" + simbolo + "\".");
            }

            // Se formatea con 3 dígitos no nulos al final y punto decimal:

            String aproximada = String.format("%.3f", masa_molecular).replace(',', '.')
                    .replaceAll("0+$", "").replaceAll("[.]+$", "");

            resultado = new MasaMolecularResultado(aproximada, elemento_a_moles, elemento_a_gramos);
        }
        else resultado = new MasaMolecularResultado("Los paréntesis no están balanceados.");

        return resultado;
    }

    public MasaMolecularResultado masaMolecular(String formula) {
        MasaMolecularResultado resultado;

        // Se comprueba si tiene aspecto de fórmula:

        String adaptada = formula.replaceAll("[ ≡=-]", "");
        if(adaptada.replaceAll("[()]", "").matches("([a-zA-Z]{1,2}([1-9]+\\d*)?[#≡=-]*)+"))
            resultado = calcularMasaMolecular(adaptada);
        else resultado = new MasaMolecularResultado("La fórmula \"" + formula + "\" no es válida.");

        return resultado;
    }

}
