package com.quimify.api;

import com.quimify.api.configuracion.ConfiguracionService;
import com.quimify.api.inorganico.InorganicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

// Esta clase contiene las órdenes se ejecutarán cuando Spring Boot indique que el servidor está
// iniciado y listo.

@Component
public class StartupListener implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    InorganicoService inorganicoService; // Procesos de los compuestos inorgánicos

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        inorganicoService.cargarSearchables();
    }
}
