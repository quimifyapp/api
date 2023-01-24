package com.quimify.api.molecular_mass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// This class implements HTTP methods in "/molecular-mass".

@RestController
@RequestMapping("/molecular-mass")
class MolecularMassController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MolecularMassService molecularMassService; // Molecular masses logic

    // Constants:

    private static final String getMolecularMassMessage = "GET molecular mass: \"%s\". RETURN: %s.";

    // Client:

    @GetMapping()
    protected MolecularMassResult calculateMolecularMassOf(@RequestParam("formula") String formula) {
        MolecularMassResult molecularMassResult = molecularMassService.tryMolecularMassResultOf(formula);

        if(molecularMassResult.isPresent())
            logger.info(String.format(getMolecularMassMessage, formula, molecularMassResult.getMolecularMass()));

        return molecularMassResult;
    }

}
