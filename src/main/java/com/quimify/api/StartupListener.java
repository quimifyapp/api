package com.quimify.api;

import com.quimify.api.inorganico.InorganicoModel;
import com.quimify.api.inorganico.InorganicoRepository;
import com.quimify.api.inorganico.InorganicoSample;
import com.quimify.api.inorganico.InorganicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

// Esta clase contiene el código que se ejecutará cuando Spring Boot indique
// que el servidor está iniciado y listo.

@Component
public class StartupListener implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    InorganicoRepository inorganicoRepository; // Conexión con la DB.

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        for(InorganicoModel inorganico : inorganicoRepository.findAll())
            InorganicoService.samples.add(new InorganicoSample(inorganico));
    }
}
