package com.quimify.api.report;

import com.quimify.api.metrics.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

// This class implements the logic behind HTTP methods in "/report".

@Service
class ReportService {

    @Autowired
    ReportRepository reportRepository; // DB connection

    @Autowired
    MetricsService metricsService;

    // Client:

    void save(String context, String details, String userMessage, Integer clientVersion) {
        ReportModel reportModel = new ReportModel();

        reportModel.setDateAndTime(Timestamp.from(Instant.now()));
        reportModel.setContext(context);
        reportModel.setDetails(details);
        reportModel.setUserMessage(userMessage);
        reportModel.setClientVersion(clientVersion);

        reportRepository.save(reportModel);

        metricsService.reportSent();
    }

}
