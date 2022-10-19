package com.quimify.api.reporte;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// Esta clase implementa los métodos HTTP de la dirección "/reporte".

@RestController
@RequestMapping("/reporte")
public class ReporteController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ReporteService reporteService; // Procesos de los reportes del cliente

    // CLIENTE ------------------------------------------------------------------------

    @PostMapping()
    public void nuevoReporte(@RequestParam("version") Integer version, @RequestParam("titulo") String titulo,
                             @RequestParam("detalles") String detalles) {
        logger.warn("Reporte de la versión " + version + " - " + titulo + " - \"" + detalles + "\".");
        reporteService.nuevoReporte(version, titulo, detalles);
    }

}
