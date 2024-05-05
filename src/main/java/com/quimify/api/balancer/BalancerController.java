package com.quimify.api.balancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/balance")
public class BalancerController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    BalancerService balancerService;

    // Constants:

    private static final String getBalancedEquationMessage = "GET balanced equation: \"%s\". RESULT: %s.";

    // Client:

    @GetMapping()
    BalancerResult balance(@RequestParam("equation") String equation) {
        BalancerResult balancerResult = balancerService.tryBalance(equation);

        if(balancerResult.isPresent())
            logger.info(String.format(getBalancedEquationMessage, equation, balancerResult.getBalancedEquation()));

        return balancerResult;
    }

    @GetMapping("/test")
    String testTemporal() {
        return balancerService.testTemporal();
    }
}