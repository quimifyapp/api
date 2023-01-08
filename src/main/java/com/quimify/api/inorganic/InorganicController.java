package com.quimify.api.inorganic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

// Esta clase implementa los métodos HTTP de la dirección "/inorganico".

@RestController
@RequestMapping("/inorganic")
class InorganicController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    InorganicService inorganicService; // Procesos de los compuestos inorgánicos

    // ADMIN --------------------------------------------------------------------------

    @PutMapping("refresh-autocompletion")
    protected void refreshAutocompletion() {
        inorganicService.refreshAutocompletion();
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
    protected @ResponseBody ResponseEntity<String> autoCompleteInorganic(@RequestParam("input") String input) {
        String completion = inorganicService.autoComplete(input);
        CacheControl cacheControl = CacheControl.maxAge(1, TimeUnit.DAYS); // Otherwise clients won't cache this
        return ResponseEntity.ok().cacheControl(cacheControl).body(completion); // Adds header to the HTTP response
    }

    @GetMapping("/from-completion")
    protected InorganicResult searchInorganicByCompletion(@RequestParam("completion") String completion) {
        InorganicResult inorganicResult = inorganicService.searchFromCompletion(completion);

        if(inorganicResult.getPresent())
            logger.info("GET inorganico: \"" + completion + "\" (compleción). RETURN: " + inorganicResult);

        return inorganicResult;
    }

}
