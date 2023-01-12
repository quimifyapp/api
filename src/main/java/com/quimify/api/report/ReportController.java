package com.quimify.api.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// This class implements HTTP methods in "/report".

@RestController
@RequestMapping("/report")
class ReportController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ReportService reportService; // Procesos de los reportes del cliente

    // Client:

    @PostMapping()
    protected void saveReport(@RequestParam("title") String title, @RequestParam("details") String details,
                              @RequestParam("client-version") Integer clientVersion) {
        logger.warn("Reporte de la versión " + clientVersion + " - " + title + " - \"" + details + "\".");
        reportService.saveReport(title, details, clientVersion);
    }

}
