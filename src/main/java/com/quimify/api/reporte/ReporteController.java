package com.quimify.api.reporte;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// Esta clase implementa los métodos HTTP de la dirección "/reporte".

@RestController
@RequestMapping("/reporte")
public class ReporteController {

    @Autowired
    ReporteService reporteService; // Procesos de los reportes

    // CLIENTE ------------------------------------------------------------------------

    @PostMapping()
    public void nuevoReporte(@RequestParam("version") Integer version, @RequestParam("titulo") String titulo,
                             @RequestParam("detalles") String detalles) {
        reporteService.nuevoReporte(version, titulo, detalles);
    }

}
