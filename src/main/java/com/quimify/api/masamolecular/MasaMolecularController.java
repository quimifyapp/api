package com.quimify.api.masamolecular;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

// Esta clase implementa los métodos HTTP de la dirección "/masamolecular".

@RestController
@RequestMapping("/masamolecular")
public class MasaMolecularController {

    @Autowired
    MasaMolecularService masaMolecularService; // Procesos de masas moleculares

    // ADMIN --------------------------------------------------------------------------

    @GetMapping() // TEST
    public Optional<Float> calcularMasaMolecular(@RequestParam("formula") String formula) {
        return masaMolecularService.tryMasaMolecular(formula);
    }

}
