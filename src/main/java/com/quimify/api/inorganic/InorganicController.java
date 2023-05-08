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

    @Autowired
    AutocompleteComponent autocompleteComponent; // Autocompletions logic

    // Constants:

    private static final String getInorganicMessage = "GET inorganic %s: \"%s\". RETURN: %s.";

    // Client:

    @GetMapping("/completion")
    protected @ResponseBody ResponseEntity<String> autoComplete(@RequestParam("input") String input) {
        String completion = autocompleteComponent.tryAutoComplete(input);
        CacheControl cacheHeader = CacheControl.empty().cachePublic(); // It allows clients and CDN to cache it
        return ResponseEntity.ok().cacheControl(cacheHeader).body(completion); // Adds header and body to the response
    }

    @GetMapping("/from-completion")
    protected InorganicResult completionSearch(@RequestParam("completion") String completion) {
        InorganicResult inorganicResult = inorganicService.searchFromCompletion(completion);

        if (inorganicResult.isPresent())
            logger.info(String.format(getInorganicMessage, "completion", completion, inorganicResult));

        return inorganicResult;
    }

    @GetMapping()
    protected InorganicResult search(@RequestParam("input") String input) {
        InorganicResult inorganicResult = inorganicService.search(input);

        if (inorganicResult.isPresent())
            logger.info(String.format(getInorganicMessage, "input", input, inorganicResult));

        return inorganicResult;
    }

    //@GetMapping("/smart")
    //protected InorganicResult smartSearch(@RequestParam("input") String input) {

    //}

    //@GetMapping("/enriched")
    //protected InorganicResult enrichedSearch(@RequestParam("input") String input) {

    //}
}
