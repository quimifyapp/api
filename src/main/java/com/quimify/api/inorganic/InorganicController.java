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

    private static final String getInorganicMessage = "GET inorganic %s: \"%s\". RESULT: %s.";

    // Client:

    @GetMapping("/completion")
    @ResponseBody
    ResponseEntity<String> complete(@RequestParam("input") String input) {
        String completion = inorganicService.complete(input);

        CacheControl cacheHeader = CacheControl.empty().cachePublic(); // It allows clients and CDN to cache it

        return ResponseEntity.ok().cacheControl(cacheHeader).body(completion); // Response has both header and body
    }

    @GetMapping("/from-completion")
    InorganicResult completionSearch(@RequestParam("completion") String completion) {
        InorganicResult inorganicResult = inorganicService.completionSearch(completion);

        if (inorganicResult.isFound())
            logger.info(String.format(getInorganicMessage, "completion", completion, inorganicResult));

        return inorganicResult;
    }

    @GetMapping()
    InorganicResult search(@RequestParam("input") String input) {
        InorganicResult inorganicResult = inorganicService.search(input);

        if (inorganicResult.isFound())
            logger.info(String.format(getInorganicMessage, "search", input, inorganicResult));

        return inorganicResult;
    }

    @GetMapping("/deep")
    InorganicResult deepSearch(@RequestParam("input") String input) {
        InorganicResult inorganicResult = inorganicService.deepSearch(input);

        if (inorganicResult.isFound())
            logger.info(String.format(getInorganicMessage, "deep search", input, inorganicResult));

        return inorganicResult;
    }
}
