package com.quimify.api.equation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/equation")
class EquationController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    EquationService equationService;

    // Constants:

    private static final String getEquationMessage = "GET equation: \"%s -> %s\". RESULT: %s -> %s.";

    // Client:

    @GetMapping()
    EquationResult balance(@RequestParam("reactants") String reactants, @RequestParam("products") String products,
                           @RequestHeader(value = "language", defaultValue = "sp") String language) {
        EquationResult equationResult = equationService.tryBalance(reactants, products, language);

        if (equationResult.isPresent())
            logger.info(String.format(getEquationMessage, reactants, products,
                    equationResult.getBalancedReactants(), equationResult.getBalancedProducts()));

        return equationResult;
    }

}