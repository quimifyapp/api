package com.quimify.servidor.metricas;

// Esta clase implementa los métodos HTTP de la dirección "/metricas".

import com.quimify.servidor.ContextoCliente;
import com.quimify.servidor.inorganico.InorganicoResultado;
import com.quimify.servidor.inorganico.InorganicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/metricas")
public class MetricasController {

    @Autowired
    MetricasService metricasService; // Procesos de las métricas diarias

    @Autowired
    InorganicoService inorganicoService; // Procesos de los compuestos inorgánicos

    // CLIENTE ------------------------------------------------------------------------

    @PutMapping("/acceso")
    public Optional<InorganicoResultado> nuevoAccesoMetricas() {
        metricasService.contarAcceso();

        return inorganicoService.inorganicoDeBienvenida();
    }

    @PutMapping("/sugerencia_ok")
    public void nuevaSugerenciaOkMetricas(@RequestParam("pantalla") Short pantalla) {
        ContextoCliente contexto = new ContextoCliente(pantalla);
        metricasService.contarSugerenciaOk(contexto);
    }

}
