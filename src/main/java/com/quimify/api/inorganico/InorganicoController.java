package com.quimify.api.inorganico;

import com.quimify.api.autorizacion.Autorizacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

// Esta clase implementa los métodos HTTP de la dirección "/inorganico".

@RestController
@RequestMapping("/inorganico")
public class InorganicoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    InorganicoService inorganicoService; // Procesos de los compuestos inorgánicos

    // CLIENTE ------------------------------------------------------------------------

    @GetMapping()
    public InorganicoResultado buscarInorganico(@RequestParam("input") String input,
                                                @RequestParam("foto") Boolean foto,
                                                @RequestHeader(HttpHeaders.AUTHORIZATION) String clave) {
        if(Autorizacion.esClavePublica(clave))
            return inorganicoService.buscar(input, foto);
        else {
            logger.error("Clave pública errónea: \"" + clave + "\".");
            return null;
        }
    }

    @GetMapping("/autocompletar/{input}")
    public String autoCompletarInorganico(@PathVariable("input") String input,
                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String clave) {
        if(Autorizacion.esClavePublica(clave))
            return inorganicoService.autoCompletar(input);
        else {
            logger.error("Clave pública errónea: \"" + clave + "\".");
            return null;
        }
    }

    @GetMapping("/autocompletar")
    public InorganicoResultado buscarComplecionInorganico(@RequestParam("complecion") String complecion,
                                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String clave) {
        if(Autorizacion.esClavePublica(clave))
            return inorganicoService.buscarComplecion(complecion);
        else {
            logger.error("Clave pública errónea: \"" + clave + "\".");
            return null;
        }
    }

    // ADMIN --------------------------------------------------------------------------

    @GetMapping("/{id}")
    public Optional<InorganicoModel> seleccionarInorganico(@PathVariable("id") Integer id,
                                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String clave) {
        if(Autorizacion.esClavePrivada(clave))
            return inorganicoService.seleccionar(id);
        else {
            logger.error("Clave privada errónea: \"" + clave + "\".");
            return Optional.empty();
        }
    }

    @PutMapping()
    public Optional<InorganicoModel> reemplazarInorganico(@RequestBody InorganicoModel nuevo,
                                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String clave) {
        if(Autorizacion.esClavePrivada(clave))
            return inorganicoService.reemplazar(nuevo);
        else {
            logger.error("Clave privada errónea: \"" + clave + "\".");
            return Optional.empty();
        }
    }

    @PutMapping("/hacerpremium")
    public Optional<InorganicoModel> hacerPremiumInorganico(@RequestParam Integer id,
                                                            @RequestHeader(HttpHeaders.AUTHORIZATION) String clave) {
        return inorganicoService.hacerPremium(id);
    }

    @PostMapping()
    public InorganicoModel insertarInorganico(@RequestBody InorganicoModel nuevo,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String clave) {
        if(Autorizacion.esClavePrivada(clave))
            return inorganicoService.insertar(nuevo);
        else {
            logger.error("Clave privada errónea: \"" + clave + "\".");
            return null;
        }
    }

    @DeleteMapping()
    public Optional<InorganicoModel> eliminarInorganico(@RequestParam("id") Integer id,
                                                        @RequestHeader(HttpHeaders.AUTHORIZATION) String clave) {
        if(Autorizacion.esClavePrivada(clave))
            return inorganicoService.eliminar(id);
        else {
            logger.error("Clave privada errónea: \"" + clave + "\".");
            return Optional.empty();
        }
    }

}
