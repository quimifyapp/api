package com.quimify.api.element;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

// This class processes chemical elements of the periodic table.

@Service
public class ElementService {

    @Autowired
    ElementRepository elementRepository; 

    // Public:

    public Optional<ElementModel> get(String symbol) {
        return elementRepository.findBySymbol(symbol);
    }

}
