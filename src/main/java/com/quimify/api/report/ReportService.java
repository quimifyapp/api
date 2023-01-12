package com.quimify.api.report;

import com.quimify.api.metrics.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

// Esta clase procesa los compuestos reportes del cliente.

@Service
class ReportService {

    @Autowired
    ReportRepository reportRepository; // Conexión con la DB

    @Autowired
    MetricsService metricsService; // Procesos de las metricas diarias

    // Client:

    protected void saveReport(String title, String details, Integer version) {
        ReportModel reportModel = new ReportModel();

        reportModel.setDateAndTime(new Timestamp(System.currentTimeMillis()));
        reportModel.setTitle(title);
        reportModel.setDetails(details);
        reportModel.setClientVersion(version);

        reportRepository.save(reportModel);

        metricsService.countReportSent();
    }

}
