package com.quimify.servidor;

import com.quimify.servidor.inorganico.InorganicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// Esta clase contiene las órdenes se ejecutarán cuando Spring Boot indique que el servidor está
// iniciado y listo.

@Component
@Transactional
public class StartupListener implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    InorganicoService inorganicoService; // Procesos de los compuestos inorgánicos

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        inorganicoService.cargarInorganicosBuscables();
    }
}
