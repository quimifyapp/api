package com.quimify.api;

import com.quimify.api.inorganico.InorganicoModel;
import com.quimify.api.inorganico.InorganicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

// Esta clase contiene el código propio que se ejecutará al iniciarse el servidor.

@Component
public class StartupListener implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    InorganicoRepository rep;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        InorganicoModel inorganico = new InorganicoModel();
        inorganico.setFormula("H2O2");
        inorganico.setNombre("peróxido de hidrógeno");

        ArrayList<String> etiquetas = new ArrayList<String>();
        etiquetas.add("hola");
        etiquetas.add("hol");
        etiquetas.add("ola");

        inorganico.setEtiquetas(etiquetas);

        rep.save(inorganico);
    }
}
