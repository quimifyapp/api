package com.quimify.api.molecular_mass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// Esta clase implementa los métodos HTTP de la dirección "/masamolecular".

@RestController
@RequestMapping("/molecular-mass")
class MolecularMassController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MolecularMassService molecularMassService; // Procesos de masas moleculares

    // CLIENTE ------------------------------------------------------------------------

    @GetMapping()
    protected MolecularMassResult calculateMolecularMassOf(@RequestParam("formula") String formula) {
        MolecularMassResult molecularMassResult = molecularMassService.tryMolecularMassResultOf(formula);

        if(molecularMassResult.getPresent())
            logger.info("GET masa molecular: \"" + formula + "\". " +
                    "RETURN: \"" + molecularMassResult.getMolecularMass() + "\".");

        return molecularMassResult;
    }

}
