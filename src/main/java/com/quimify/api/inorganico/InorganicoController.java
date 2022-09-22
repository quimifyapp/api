package com.quimify.api.inorganico;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// Esta clase implementa los métodos HTTP de la dirección "/inorganico".

@RestController
@RequestMapping("/inorganico")
public class InorganicoController {

    @Autowired
    InorganicoService inorganicoService; // Procesos de los compuestos inorgánicos

    // ADMIN --------------------------------------------------------------------------

    @GetMapping("/autocompletar/actualizar")
    public void actualizarNormalizados() {
        inorganicoService.cargarNormalizados();
    }

    // CLIENTE ------------------------------------------------------------------------

    @GetMapping()
    public InorganicoResultado buscarInorganico(@RequestParam("input") String input,
                                                @RequestParam("foto") Boolean foto) {
        return inorganicoService.buscar(input, foto);
    }

    @GetMapping("/autocompletar/{input}")
    public String autoCompletarInorganico(@PathVariable("input") String input) {
        return inorganicoService.autoCompletar(input);
    }

    @GetMapping("/autocompletar")
    public InorganicoResultado buscarComplecionInorganico(@RequestParam("complecion") String complecion) {
        return inorganicoService.buscarPorComplecion(complecion);
    }

}
