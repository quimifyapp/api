package com.quimify.api.clienterror;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// This class implements HTTP methods in "/client-error".

@RestController
@RequestMapping("/client-error")
class ClientErrorController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ClientErrorService clientErrorService; // Client errors logic

    // Constants:

    private static final String postClientErrorMessage = "POST error from V%s client. Details saved in database.";

    // Client:

    @PostMapping()
    void save(@RequestParam("context") String context, @RequestParam("details") String details,
                        @RequestParam("client-version") Integer clientVersion) {
        clientErrorService.save(context, details, clientVersion);
        logger.warn(String.format(postClientErrorMessage, clientVersion));
    }

}
