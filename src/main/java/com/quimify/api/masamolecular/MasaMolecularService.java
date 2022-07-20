package com.quimify.api.masamolecular;

import com.quimify.api.elemento.ElementoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

// Esta clase procesa las masas moleculares.

// TODO: Solo funciona para compuestos formados por un anión y un catión. Ejemplo: Na ó NaCl

@Service
public class MasaMolecularService {

    @Autowired
    ElementoService elementoService; // Procesos de los elementos

    // INTERNOS ----------------------------------------------------------------------

    private Float masaElemento(String simbolo) throws NoSuchElementException {
        return elementoService.buscarSimbolo(simbolo).get().getMasa();
    }

    private float masaMolecular(String formula) throws NoSuchElementException {
        float resultado = 0;

        String[] partes = formula.split("(?<=.)(?=\\p{Lu})|(?<=\\d)(?=\\p{Lu})"); // "Fe2O3" -> ("Fe2", "O3")

        for(String parte : partes) {
            StringBuilder simbolo = new StringBuilder();
            StringBuilder numero = new StringBuilder();

            // Separa la parte:
            for(int i = 0; i < parte.length(); i++)
                if(Character.isLetter(parte.charAt(i)))
                    simbolo.append(parte.charAt(i));
                else if(Character.isDigit(parte.charAt(i)))
                    numero.append(parte.charAt(i));

            resultado += (numero.length() != 0)
                    ? masaElemento(simbolo.toString()) * Integer.parseInt(numero.toString())
                    : masaElemento(simbolo.toString());
        }

        return resultado;
    }

    public Optional<Float> tryMasaMolecular(String formula) {
        Optional<Float> resultado;

        formula = formula.replaceAll(" ", ""); // Sin espacios
        try {
            if(formula.matches("[a-zA-Z0-9()-=≡]+")) // La fórmula es alfanumérica con: (, ), -, =, ≡
                resultado = Optional.of(masaMolecular(formula));
            else resultado = Optional.empty();
        } catch (NoSuchElementException e) {
            // Error...
            resultado = Optional.empty();
        }

        return resultado;
    }

}
