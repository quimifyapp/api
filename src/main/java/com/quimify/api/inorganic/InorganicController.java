package com.quimify.api.inorganic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// Esta clase implementa los métodos HTTP de la dirección "/inorganico".

@RestController
@RequestMapping("/inorganic")
public class InorganicController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    InorganicService inorganicService; // Procesos de los compuestos inorgánicos

    // ADMIN --------------------------------------------------------------------------

    @GetMapping("autocomplete/update")
    public void updateNormalizedInorganics() {
        inorganicService.loadNormalizedInorganics();
    }

    // CLIENTE ------------------------------------------------------------------------

    @GetMapping()
    public InorganicResult searchInorganic(@RequestParam("input") String input,
                                           @RequestParam("picture") Boolean picture) {
        InorganicResult inorganicResult = inorganicService.search(input, picture);

        if(inorganicResult.getPresent())
            logger.info("GET inorganico: \"" + input + "\" (" + (picture ? "foto" : "teclado") + "). " +
                    "RETURN: " + inorganicResult);

        return inorganicResult;
    }

    @GetMapping("/autocomplete")
    public String autoCompleteInorganic(@RequestParam("input") String input) {
        return inorganicService.autoComplete(input);
    }

    @GetMapping("/autocompletar/buscar")
    public InorganicResult searchInorganicByCompletion(@RequestParam("completion") String completion) {
        InorganicResult inorganicResult = inorganicService.searchFromCompletion(completion);

        if(inorganicResult.getPresent())
            logger.info("GET inorganico: \"" + completion + "\" (compleción). RETURN :" + inorganicResult);

        return inorganicResult;
    }

}
