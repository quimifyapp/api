package com.quimify.api.elemento;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

// Esta clase procesa los elementos químicos.

@Service
public class ElementoService {

    @Autowired
    ElementoRepository elementoRepository; // Conexión con la DB

    // INTERNOS ----------------------------------------------------------------------

    public Optional<ElementoModel> buscarSimbolo(String simbolo) {
        return elementoRepository.findBySimbolo(simbolo);
    }

}
