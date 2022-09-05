package com.quimify.servidor.metricas;

// Esta clase implementa los métodos HTTP de la dirección "/metricas".

import com.quimify.servidor.inorganico.InorganicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/metricas")
public class MetricasController {

    @Autowired
    MetricasService metricasService; // Procesos de las métricas diarias

    @Autowired
    InorganicoService inorganicoService; // Procesos de los compuestos inorgánicos

    // CLIENTE ------------------------------------------------------------------------



}
