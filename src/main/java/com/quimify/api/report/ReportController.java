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
    ReportService reportService; // Client reports logic

    // Constants:

    private static final String postReportMessage = "POST report from V%s client. Details saved in database.";

    // Client:

    @PostMapping()
    protected void save(@RequestParam("context") String context, @RequestParam("details") String details,
                        @RequestParam("user-message") String userMessage,
                        @RequestParam("client-version") Integer clientVersion) {
        reportService.save(context, details, userMessage, clientVersion);
        logger.warn(String.format(postReportMessage, clientVersion));
    }

}
