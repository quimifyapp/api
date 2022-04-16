package com.quimify.api.services;

// Esta clase procesa los compuestos inorgánicos y realiza conexiones con la DB.

// Inorganico = (id, busquedas, premium, formula, nombre, alternativo, masa, densidad, fu., eb.)

import com.quimify.api.models.InorganicoModel;
import com.quimify.api.results.InorganicoResult;
import com.quimify.api.repositories.InorganicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class InorganicoService {
    @Autowired
    InorganicoRepository inorganicoRepository; // Conexión con la DB.

    private final static InorganicoResult NO_ENCONTRADO =
            new InorganicoResult(InorganicoResult.NO_ENCONTRADO);
    private final static InorganicoResult PREMIUM =
            new InorganicoResult(InorganicoResult.PREMIUM);

    public ArrayList<InorganicoModel> obtenerTodos() {
        return (ArrayList<InorganicoModel>) inorganicoRepository.findAll();
    }

    public InorganicoModel guardarInorganico(InorganicoModel inorganico) {
        return inorganicoRepository.save(inorganico);
    }

    public InorganicoResult buscar(String input, Boolean premium) {
        InorganicoResult resultado;

        try {
            ArrayList<InorganicoModel> identicos = inorganicoRepository.findByFormula(input);
            resultado = identicos.size() != 0
                    ? identicos.get(0).getPremium() && !premium
                        ? PREMIUM
                        : new InorganicoResult(identicos.get(0))
                    : NO_ENCONTRADO;
        } catch (Exception error) {
            // ...
            resultado = NO_ENCONTRADO;
        }

        return resultado;
    }

}
