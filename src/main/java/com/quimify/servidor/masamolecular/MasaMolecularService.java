package com.quimify.servidor.masamolecular;

import com.quimify.servidor.elemento.ElementoModel;
import com.quimify.servidor.elemento.ElementoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

// Esta clase procesa las masas moleculares.

@Service
public class MasaMolecularService {

    @Autowired
    ElementoService elementoService; // Procesos de los elementos

    // INTERNOS ----------------------------------------------------------------------

    private Optional<Float> getMasaElemento(String simbolo) {
        return elementoService.buscarSimbolo(simbolo).map(ElementoModel::getMasa);
    }

    public MasaMolecularResultado calcularMasaMolecular(String formula) {
        MasaMolecularResultado resultado;

        // Se comprueba si tiene aspecto de fórmula:

        String adaptada = formula.replaceAll("[ ≡=-]", "");

        if(adaptada.matches("([A-Z]+[a-z]?([1-9]+[0-9]*)?[#≡=-]*)+")) {
            String[] partes = adaptada.split("(?=[A-Z])"); // "Aa11Bb22" -> ("Aa11", "Bb22")

            // Se registran los elementos y sus moles:

            Map<String, Integer> elemento_a_moles = new HashMap<>();

            for(String parte : partes) {
                String simbolo = parte.replaceAll("[0-9]", "");
                String digitos = parte.replaceAll("[A-Za-z]", "");

                // Registra el elemento:

                int moles = digitos.length() > 0 ? Integer.parseInt(digitos) : 1;

                Integer moles_previos = elemento_a_moles.get(simbolo);
                if(moles_previos != null)
                    elemento_a_moles.replace(simbolo, moles_previos + moles);
                else elemento_a_moles.put(simbolo, moles);
            }

            // Se calculan los gramos de cada elemento y el total es la masa molecular:

            float masa_molecular = 0;
            Map<String, Float> elemento_a_gramos = new HashMap<>();

            for(Map.Entry<String, Integer> elemento : elemento_a_moles.entrySet()) {
                String simbolo = elemento.getKey();

                Optional<Float> masa_elemento = getMasaElemento(simbolo);

                if(masa_elemento.isPresent()) {
                    float gramos = elemento.getValue() * masa_elemento.get();
                    masa_molecular += gramos;
                    elemento_a_gramos.put(simbolo, gramos);
                }
                else return new MasaMolecularResultado("No se reconoce el elemento \"" + simbolo + "\".");
            }

            // Se formatea con 3 dígitos no nulos al final y punto decimal:

            String aproximada = String.format("%.3f", masa_molecular).replace(',', '.')
                    .replaceAll("0+$", "").replaceAll("[.]+$", "");

            resultado = new MasaMolecularResultado(aproximada, elemento_a_gramos);
        }
        else resultado = new MasaMolecularResultado("La fórmula \"" + formula + "\" no es válida.");

        return resultado;
    }

}
