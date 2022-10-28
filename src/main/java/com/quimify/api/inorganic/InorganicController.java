package com.quimify.api.inorganic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// Esta clase implementa los métodos HTTP de la dirección "/inorganico".

@RestController
@RequestMapping("/inorganic")
class InorganicController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    InorganicService inorganicService; // Procesos de los compuestos inorgánicos

    // ADMIN --------------------------------------------------------------------------

    @PutMapping("update-completions")
    protected void updateNormalizedInorganics() {
        inorganicService.loadNormalizedInorganics();
    }

    // CLIENTE ------------------------------------------------------------------------

    @GetMapping()
    protected InorganicResult searchInorganic(@RequestParam("input") String input,
                                           @RequestParam("picture") Boolean picture) {
        InorganicResult inorganicResult = inorganicService.search(input, picture);

        if(inorganicResult.getPresent())
            logger.info("GET inorganico: \"" + input + "\" (" + (picture ? "foto" : "teclado") + "). " +
                    "RETURN: " + inorganicResult);

        return inorganicResult;
    }

    @GetMapping("/completion")
    protected String autoCompleteInorganic(@RequestParam("input") String input) {
        return inorganicService.autoComplete(input);
    }

    @GetMapping("/from-completion")
    protected InorganicResult searchInorganicByCompletion(@RequestParam("completion") String completion) {
        InorganicResult inorganicResult = inorganicService.searchFromCompletion(completion);

        if(inorganicResult.getPresent())
            logger.info("GET inorganico: \"" + completion + "\" (compleción). RETURN :" + inorganicResult);

        return inorganicResult;
    }

}
