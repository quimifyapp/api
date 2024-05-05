package com.quimify.api.balancer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/balance")
class BalancerController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    BalancerService balancerService;

    // Constants:

    private static final String getBalancedEquationMessage = "GET equation: \"%s -> %s\". RESULT: %s -> %s.";

    // Client:

    @GetMapping()
    BalancerResult balance(@RequestParam("reactants") String reactants, @RequestParam("products") String products) {
        BalancerResult balancerResult = balancerService.tryBalance(reactants, products);

        if (balancerResult.isPresent())
            logger.info(String.format(getBalancedEquationMessage, reactants, products,
                    balancerResult.getBalancedReactants(), balancerResult.getBalancedProducts()));

        return balancerResult;
    }

    // TODO remove
    @GetMapping("/test")
    String testTemporal() {
        return balancerService.testTemporal();
    }

}