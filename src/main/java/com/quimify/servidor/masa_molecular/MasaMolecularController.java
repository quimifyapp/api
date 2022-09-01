package com.quimify.servidor.masa_molecular;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


// Esta clase implementa los métodos HTTP de la dirección "/masamolecular".

@RestController
@RequestMapping("/masamolecular")
public class MasaMolecularController {

    @Autowired
    MasaMolecularService masaMolecularService; // Procesos de masas moleculares

    // ADMIN --------------------------------------------------------------------------

    @GetMapping()
    public MasaMolecularResultado masaMolecular(@RequestParam("formula") String formula) {
        return masaMolecularService.tryMasaMolecularDe(formula);
    }

}
