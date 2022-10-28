package com.quimify.api.reporte;

import com.quimify.api.metrics.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Esta clase procesa los compuestos reportes del cliente.

@Service
class ReportService {

    @Autowired
    ReportRepository reportRepository; // Conexión con la DB

    @Autowired
    MetricsService metricsService; // Procesos de las metricas diarias

    // CLIENTE -----------------------------------------------------------------------

    protected void postReport(Integer version, String titulo, String detalles) {
        ReportModel reporte = new ReportModel();

        reporte.setClientVersion(version);
        reporte.setTitle(titulo);
        reporte.setDetails(detalles);

        reportRepository.save(reporte);

        metricsService.contarReporte();
    }

}
