package com.quimify.api.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// This class implements HTTP methods in "/client".

@RestController
@RequestMapping("/client")
class ClientController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ClientService clientService;

    // Constants:

    private static final String getAccessDataMessage = "GET client from \"%s\" version %s.";

    // Client:

    @GetMapping()
    ClientResult get(@RequestParam("platform") String platform, @RequestParam("version") Integer version) {
        logger.info(String.format(getAccessDataMessage, platform, version));

        return clientService.get(platform, version);
    }

}
