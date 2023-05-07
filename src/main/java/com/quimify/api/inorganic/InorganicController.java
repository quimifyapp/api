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

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    InorganicService inorganicService;

    // Constants:

    private static final String getInorganicMessage = "GET inorganic %s: \"%s\". RETURN: %s.";

    // Client:

    @GetMapping("/completion")
    @ResponseBody ResponseEntity<String> complete(@RequestParam("input") String input) {
        String completion = inorganicService.complete(input);
        CacheControl cacheHeader = CacheControl.empty().cachePublic(); // It allows clients and CDN to cache it
        return ResponseEntity.ok().cacheControl(cacheHeader).body(completion); // Response has both header and body
    }

    @GetMapping("/from-completion")
    InorganicResult searchFromCompletion(@RequestParam("completion") String completion) {
        InorganicResult inorganicResult = inorganicService.searchFromCompletion(completion);

        if (inorganicResult.isPresent())
            logger.info(String.format(getInorganicMessage, "completion", completion, inorganicResult));

        return inorganicResult;
    }

    @GetMapping()
    InorganicResult search(@RequestParam("input") String input) {
        InorganicResult inorganicResult = inorganicService.search(input);

        if (inorganicResult.isPresent())
            logger.info(String.format(getInorganicMessage, "input", input, inorganicResult));

        return inorganicResult;
    }

    //@GetMapping("/smart")
    //InorganicResult smartSearch(@RequestParam("input") String input) {

    //}

    //@GetMapping("/enriched")
    //InorganicResult enrichedSearch(@RequestParam("input") String input) {

    //}
}
