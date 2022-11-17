package com.quimify.api.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// Esta clase implementa los métodos HTTP de la dirección "/reporte".

@RestController
@RequestMapping("/report")
class ReportController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ReportService reportService; // Procesos de los reportes del cliente

    // CLIENTE ------------------------------------------------------------------------

    @PostMapping()
    protected void postReport(@RequestParam("title") String title, @RequestParam("details") String details,
                              @RequestParam("client-version") Integer clientVersion) {
        logger.warn("Reporte de la versión " + clientVersion + " - " + title + " - \"" + details + "\".");
        reportService.postReport(title, details, clientVersion);
    }

}
