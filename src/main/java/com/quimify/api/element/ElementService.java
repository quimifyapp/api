package com.quimify.api.element;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

// Esta clase procesa los elementos químicos.

@Service
public
class ElementService {

    @Autowired
    ElementRepository elementRepository; // Conexión con la DB

    // Public:

    public Optional<ElementModel> searchBySymbol(String symbol) {
        return elementRepository.findBySymbol(symbol);
    }

}
