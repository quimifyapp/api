package com.quimify.api.masa_molecular;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// Esta clase implementa los métodos HTTP de la dirección "/masamolecular".

@RestController
@RequestMapping("/masamolecular")
public class MasaMolecularController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MasaMolecularService masaMolecularService; // Procesos de masas moleculares

    // CLIENTE ------------------------------------------------------------------------

    @GetMapping()
    public MasaMolecularResultado masaMolecular(@RequestParam("formula") String formula) {
        MasaMolecularResultado masaMolecularResultado = masaMolecularService.tryMasaMolecularDe(formula);

        if(masaMolecularResultado.getEncontrado())
            logger.info("GET masa molecular: \"" + formula + "\". " +
                    "RETURN: \"" + masaMolecularResultado.getMasa() + "\".");

        return masaMolecularResultado;
    }

}
