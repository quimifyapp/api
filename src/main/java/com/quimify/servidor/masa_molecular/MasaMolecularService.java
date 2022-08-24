package com.quimify.servidor.masa_molecular;

import com.quimify.servidor.elemento.ElementoModel;
import com.quimify.servidor.elemento.ElementoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    private Optional<Map<String, Integer>> calcularMolesDeElementosEn(String formula) {
        Optional<Map<String, Integer>> resultado;

        // Separa las fórmulas anidadas entre paréntesis:

        int balance = 0;
        Map<String, Integer> anidada_a_moles = new HashMap<>();

        for(int i = 0, parentesis = 0; i < formula.length(); i++) {
            if(formula.charAt(i) == ')') {
                balance -= 1;

                if(balance == 0) { // Implica que hay '(' antes
                    StringBuilder anidada = new StringBuilder(formula.substring(parentesis + 1, i));

                    // Se procesan los cuantificadores:

                    i += 1; // Lo siguiente tras el último paréntesis

                    int moles;
                    if(i < formula.length()) {
                        String digitos = formula.substring(i);
                        if(digitos.matches("^\\d+.*")) {
                            digitos = digitos.replaceAll("^(\\d+).+", "$1");
                            moles = digitos.length() > 0 ? Integer.parseInt(digitos) : 1;
                        }
                        else moles = 1;
                    }
                    else moles = 1;

                    agregarAlMapa(anidada.toString(), moles, anidada_a_moles); // Registra la fórmula anidada

                    // Finalmente:

                    formula = formula.replace("(" + anidada + ")" + (moles != 1 ? moles : ""), "");
                    i = parentesis - 1; // Continúa donde estaba el primer paréntesis (luego se incrementa en 1)
                }
                else if(balance < 0)
                    return Optional.empty();
            }
            else if(formula.charAt(i) == '(') {
                if(balance == 0)
                    parentesis = i;

                balance += 1;
            }
        }

        // Se suman las masas de las fórmulas anidadas (recursivo):

        Map<String, Integer> elemento_a_moles = new HashMap<>();

        for(Map.Entry<String, Integer> anidada : anidada_a_moles.entrySet()) {
            Optional<Map<String, Integer>> anidados = calcularMolesDeElementosEn(anidada.getKey());

            if(anidados.isPresent()) {
                for(Map.Entry<String, Integer> elemento : anidados.get().entrySet())
                    agregarAlMapa(elemento.getKey(), elemento.getValue() * anidada.getValue(), elemento_a_moles);
            }
            else return Optional.empty();
        }

        // Procesa lo que no va entre paréntesis:

        if(balance == 0 && !(formula.contains("(") || formula.contains(")"))) { // No queda ningún paréntesis suelto
            if(!formula.equals("")) { // Puede pasar con fórmulas como "(NaCl)3" -> "()3"
                String[] partes = formula.split("(?=[A-Z])"); // "Aa11Bb22" -> ("Aa11", "Bb22")

                // Se registran los elementos y sus moles:

                for(String parte : partes) {
                    String simbolo = parte.replaceAll("\\d", "");
                    String digitos = parte.replaceAll("[A-Za-z]", "");
                    int moles = digitos.length() > 0 ? Integer.parseInt(digitos) : 1;

                    agregarAlMapa(simbolo, moles, elemento_a_moles); // Registra el elemento
                }
            }

            resultado = Optional.of(elemento_a_moles);
        }
        else resultado = Optional.empty();

        return resultado;
    }

    public MasaMolecularResultado masaMolecular(String formula) {
        // Se comprueba si tiene aspecto de fórmula:

        String adaptada = formula.replaceAll("[ #≡=-]", ""); // Sin espacios

        if(!adaptada.matches("(\\(*[A-Z][a-z]?(([1-9]\\d+)|([2-9]))?((\\(*)|(\\)(([1-9]\\d+)|([2-9]))?))*)+"))
            return new MasaMolecularResultado("La fórmula \"" + formula + "\" no es válida.");
        else if(StringUtils.countOccurrencesOf(adaptada, "(") != StringUtils.countOccurrencesOf(adaptada, ")"))
            return new MasaMolecularResultado("Los paréntesis no están balanceados.");
        else if(adaptada.contains("()"))
            return new MasaMolecularResultado("Los paréntesis huecos \"()\" no son válidos.");

        // Parece que sí:

        MasaMolecularResultado resultado;

        Optional<Map<String, Integer>> elemento_a_moles = calcularMolesDeElementosEn(adaptada); // Se analiza la fórmula

        // Se calcula la masa molecular:

        if(elemento_a_moles.isPresent()) {
            float masa_molecular = 0;
            Map<String, Float> elemento_a_gramos = new HashMap<>();

            // Se calculan los gramos de cada elemento, siendo el total la masa molecular:

            for(Map.Entry<String, Integer> elemento : elemento_a_moles.get().entrySet()) {
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

            resultado = new MasaMolecularResultado(aproximada, elemento_a_moles.get(), elemento_a_gramos);
        }
        else return new MasaMolecularResultado("No se ha podido calcular la masa molecular.");

        return resultado;
    }

}
