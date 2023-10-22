package com.quimify.api.molecularmass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// This class implements HTTP methods in "/molecular-mass".

@RestController
@RequestMapping("/molecular-mass")
class MolecularMassController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    MolecularMassService molecularMassService;

    // Constants:

    private static final String getMolecularMassMessage = "GET molecular mass: \"%s\". RESULT: %s.";

    // Client:

    @GetMapping()
    MolecularMassResult calculate(@RequestParam("formula") String formula) {
        MolecularMassResult molecularMassResult = molecularMassService.tryCalculate(formula);

        if(molecularMassResult.isPresent())
            logger.info(String.format(getMolecularMassMessage, formula, molecularMassResult.getMolecularMass()));

        return molecularMassResult;
    }

}
