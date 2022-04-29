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

    @GetMapping()
    public InorganicoResultado buscar(@RequestParam("input") String input,
                                      @RequestParam("premium") Boolean premium) {
        return inorganicoService.buscar(input, premium);
    }

    @GetMapping("/autocompletar")
    public InorganicoResultado autoCompletar(@RequestParam("input") String input) {
        return inorganicoService.autoCompletar(input);
    }

    @GetMapping("/todos") // Test
    public ArrayList<InorganicoModel> obtenerTodos() {
        return inorganicoService.obtenerTodos();
    }

    @PostMapping("/guardar") // Test
    public InorganicoModel guardarInorganico(@RequestBody InorganicoModel inorganico) {
        return inorganicoService.insertarInorganico(inorganico);
    }

}
