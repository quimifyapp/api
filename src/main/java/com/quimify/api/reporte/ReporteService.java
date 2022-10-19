package com.quimify.api.reporte;

import com.quimify.api.metricas.MetricasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Esta clase procesa los compuestos reportes del cliente.

@Service
public class ReporteService {

    @Autowired
    ReporteRepository reporteRepository; // Conexión con la DB

    @Autowired
    MetricasService metricasService; // Procesos de las metricas diarias

    // CLIENTE -----------------------------------------------------------------------

    public void nuevoReporte(Integer version, String titulo, String detalles) {
        ReporteModel reporte = new ReporteModel();

        reporte.setVersion(version);
        reporte.setTitulo(titulo);
        reporte.setDetalles(detalles);

        reporteRepository.save(reporte);

        metricasService.contarReporte();
    }

}
