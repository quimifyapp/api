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
    InorganicService inorganicService; // Inorganic compounds logic

    // Constants:

    private static final String getInorganicMessage = "GET inorganic %s: \"%s\". RETURN: %s.";

    // Client:

    @GetMapping()
    protected InorganicResult searchInorganic(@RequestParam("input") String input, @RequestParam("picture") Boolean p) {
        InorganicResult inorganicResult = inorganicService.search(input);

        if(inorganicResult.isPresent())
            logger.info(String.format(getInorganicMessage, "input", input, inorganicResult));

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

        if(inorganicResult.isPresent())
            logger.info(String.format(getInorganicMessage, "completion", completion, inorganicResult));

        return inorganicResult;
    }

}
