package com.quimify.api.inorganico;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

// Esta clase implementa los métodos HTTP de la dirección "/inorganico".

@RestController
@RequestMapping("/inorganico")
public class InorganicoController {

    @Autowired
    InorganicoService inorganicoService; // Procesos de los compuestos inorgánicos

    // ADMIN --------------------------------------------------------------------------

    @GetMapping("/probarfq") // TEST
    public InorganicoResultado probarPaginaFQ(@RequestParam("direccion") String direccion) {
        return inorganicoService.probarPaginaFQ(direccion);
    }

    @GetMapping("/seleccionar")
    public Optional<InorganicoModel> actualizarInorganico(@RequestParam("id") Integer id) {
        return inorganicoService.seleccionar(id);
    }

    @PutMapping("/reemplazar")
    public Optional<InorganicoModel> reemplazarInorganico(@RequestBody InorganicoModel nuevo) {
        return inorganicoService.reemplazar(nuevo);
    }

    @PostMapping("/insertar")
    public Optional<InorganicoModel> insertarInorganico(@RequestBody InorganicoModel nuevo) {
        return inorganicoService.insertar(nuevo);
    }

    @DeleteMapping("/eliminar")
    public Optional<InorganicoModel> eliminarInorganico(@RequestParam("id") Integer id) {
        return inorganicoService.eliminar(id);
    }

    // CLIENTE ------------------------------------------------------------------------

    @GetMapping()
    public InorganicoResultado buscarInorganico(@RequestParam("input") String input,
                                      @RequestParam("usuario_premium") Boolean usuario_premium) {
        return inorganicoService.buscar(input, usuario_premium);
    }

    @GetMapping("/autocompletar")
    public InorganicoResultado autoCompletarInorganico(@RequestParam("input") String input) {
        return inorganicoService.autoCompletar(input);
    }

}
