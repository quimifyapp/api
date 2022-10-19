package com.quimify.api.inorganico;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// Esta clase implementa los métodos HTTP de la dirección "/inorganico".

@RestController
@RequestMapping("/inorganico")
public class InorganicoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    InorganicoService inorganicoService; // Procesos de los compuestos inorgánicos

    // ADMIN --------------------------------------------------------------------------

    @GetMapping("autocompletar/actualizar")
    public void actualizarNormalizados() {
        inorganicoService.cargarNormalizados();
    }

    // CLIENTE ------------------------------------------------------------------------

    @GetMapping()
    public InorganicoResultado buscarInorganico(@RequestParam("input") String input,
                                                @RequestParam("foto") Boolean foto) {
        logger.info("GET inorganico: \"" + input + "\" (" + (foto ? "foto" : "teclado") + ").");
        return inorganicoService.buscar(input, foto);
    }

    @GetMapping("/autocompletar")
    public String autoCompletarInorganico(@RequestParam("input") String input) {
        return inorganicoService.autoCompletar(input);
    }

    @GetMapping("/autocompletar/buscar")
    public InorganicoResultado buscarComplecionInorganico(@RequestParam("complecion") String complecion) {
        logger.info("GET inorganico: \"" + complecion + "\" (compleción).");
        return inorganicoService.buscarPorComplecion(complecion);
    }

}
