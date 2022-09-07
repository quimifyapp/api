package com.quimify.servidor.elemento;

import com.quimify.servidor.autentificacion.Autentificacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

// Esta clase implementa los métodos HTTP de la dirección "/elemento".

@RestController
@RequestMapping("/elemento")
public class ElementoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ElementoService elementoService; // Procesos de los elementos

    // ADMIN --------------------------------------------------------------------------

    @GetMapping("/{id}")
    public Optional<ElementoModel> seleccionarElemento(@PathVariable("id") Integer id,
                                                       @RequestParam("clave") String clave) {
        if(Autentificacion.esClavePrivada(clave))
            return elementoService.seleccionar(id);
        else {
            logger.error("Clave privada errónea: \"" + clave + "\".");
            return Optional.empty();
        }
    }

    @PutMapping()
    public Optional<ElementoModel> reemplazarElemento(@RequestBody ElementoModel nuevo,
                                                      @RequestParam("clave") String clave) {
        if(Autentificacion.esClavePrivada(clave))
            return elementoService.reemplazar(nuevo);
        else {
            logger.error("Clave privada errónea: \"" + clave + "\".");
            return Optional.empty();
        }
    }

    @PostMapping()
    public ElementoModel insertarElemento(@RequestBody ElementoModel nuevo,
                                          @RequestParam("clave") String clave) {
        if(Autentificacion.esClavePrivada(clave))
            return elementoService.insertar(nuevo);
        else {
            logger.error("Clave privada errónea: \"" + clave + "\".");
            return null;
        }
    }

    @DeleteMapping()
    public Optional<ElementoModel> eliminarElemento(@RequestParam("id") Integer id,
                                                    @RequestParam("clave") String clave) {
        if(Autentificacion.esClavePrivada(clave))
            return elementoService.eliminar(id);
        else {
            logger.error("Clave privada errónea: \"" + clave + "\".");
            return Optional.empty();
        }
    }

}
