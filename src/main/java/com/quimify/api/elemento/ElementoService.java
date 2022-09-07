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

    // ADMIN --------------------------------------------------------------------------

    public Optional<ElementoModel> seleccionar(Integer id) {
        return elementoRepository.findById(id);
    }

    public Optional<ElementoModel> reemplazar(ElementoModel nuevo) {
        Optional<ElementoModel> reemplazado = elementoRepository.findById(nuevo.getId());

        if(reemplazado.isPresent()) // Si existe
            elementoRepository.save(nuevo); // De la DB

        return reemplazado;
    }

    public ElementoModel insertar(ElementoModel nuevo) {
        return elementoRepository.save(nuevo); // En la DB
    }

    public Optional<ElementoModel> eliminar(Integer id) {
        Optional<ElementoModel> eliminado = elementoRepository.findById(id);

        if(eliminado.isPresent())
            elementoRepository.deleteById(id); // De la DB

        return eliminado;
    }

}
