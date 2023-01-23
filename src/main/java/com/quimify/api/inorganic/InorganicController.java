package com.quimify.api.inorganic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// This class implements HTTP methods in "/inorganic".

@RestController
@RequestMapping("/inorganic")
class InorganicController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    InorganicService inorganicService; // Procesos de los compuestos inorgánicos

    // Administration:

    @PutMapping("refresh-autocompletion")
    protected void refreshAutocompletion() {
        inorganicService.refreshAutocompletion();
    }

    // Client:

    @GetMapping()
    protected InorganicResult searchInorganic(@RequestParam("input") String input,
                                              @RequestParam("picture") Boolean picture) {
        InorganicResult inorganicResult = inorganicService.search(input, picture);

        if(inorganicResult.getPresent())
            logger.info("GET inorganico: \"" + input + "\". " + "RETURN: " + inorganicResult);

        return inorganicResult;
    }

    @GetMapping("/completion")
    protected @ResponseBody ResponseEntity<String> autoCompleteInorganic(@RequestParam("input") String input) {
        String completion = inorganicService.autoComplete(input);
        CacheControl cacheHeader = CacheControl.empty().cachePublic(); // It allows clients and CDN to cache it
        return ResponseEntity.ok().cacheControl(cacheHeader).body(completion); // Adds header and body to the response
    }

    @GetMapping("/from-completion")
    protected InorganicResult searchInorganicByCompletion(@RequestParam("completion") String completion) {
        InorganicResult inorganicResult = inorganicService.searchFromCompletion(completion);

        if(inorganicResult.getPresent())
            logger.info("GET inorganico: \"" + completion + "\" (autocompleted). RETURN: " + inorganicResult);

        return inorganicResult;
    }

}
