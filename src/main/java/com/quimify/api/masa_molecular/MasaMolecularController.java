package com.quimify.api.masa_molecular;

import com.quimify.api.autorizacion.Autorizacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

// Esta clase implementa los métodos HTTP de la dirección "/masamolecular".

@RestController
@RequestMapping("/masamolecular")
public class MasaMolecularController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MasaMolecularService masaMolecularService; // Procesos de masas moleculares

    // CLIENTE ------------------------------------------------------------------------

    @GetMapping()
    public MasaMolecularResultado masaMolecular(@RequestParam("formula") String formula,
                                                @RequestHeader(HttpHeaders.AUTHORIZATION) String clave) {
        if(Autorizacion.esClavePublica(clave))
            return masaMolecularService.tryMasaMolecularDe(formula);
        else {
            logger.error("Clave pública errónea: \"" + clave + "\".");
            return null;
        }
    }

}
