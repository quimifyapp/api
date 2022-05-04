package com.quimify.api.inorganico;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

// Esta clase implementa los métodos HTTP de la dirección "/inorganico".

@RestController
@RequestMapping("/inorganico")
public class InorganicoController {

    @Autowired
    InorganicoService inorganicoService; // Procesos de los compuestos inorgánicos

    // --------------------------------------------------------------------------------

    @GetMapping("/todos") // TEST
    public ArrayList<InorganicoModel> obtenerTodos() {
        return inorganicoService.obtenerTodos();
    }

    @PostMapping("/guardar") // TEST
    public InorganicoModel guardarInorganico(@RequestBody InorganicoModel inorganico) {
        return inorganicoService.insertarInorganico(inorganico);
    }

    @GetMapping("/probarfq") // TEST
    public InorganicoResultado probarPaginaFQ(@RequestParam("direccion") String direccion) {
        return inorganicoService.probarPaginaFQ(direccion);
    }

    // --------------------------------------------------------------------------------

    @GetMapping()
    public InorganicoResultado buscar(@RequestParam("input") String input,
                                      @RequestParam("usuario_premium") Boolean usuario_premium) {
        return inorganicoService.buscar(input, usuario_premium);
    }

    @GetMapping("/autocompletar")
    public InorganicoResultado autoCompletar(@RequestParam("input") String input) {
        return inorganicoService.autoCompletar(input);
    }

}
