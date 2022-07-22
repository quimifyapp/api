package com.quimify.api.inorganico;

import com.quimify.api.ContextoCliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

// Esta clase implementa los métodos HTTP de la dirección "/inorganico".

@RestController
@RequestMapping("/inorganico")
public class InorganicoController {

    @Autowired
    InorganicoService inorganicoService; // Procesos de los compuestos inorgánicos

    // CLIENTE ------------------------------------------------------------------------

    @GetMapping()
    public InorganicoResultado buscarInorganico(@RequestParam("input") String input,
                                                @RequestParam("pantalla") Short pantalla,
                                                @RequestParam("premium") Boolean premium) {
        ContextoCliente contexto = new ContextoCliente(pantalla, premium);
        return inorganicoService.buscar(input, contexto);
    }

    @GetMapping("/autocompletar/{input}")
    public String autoCompletarInorganico(@PathVariable("input") String input) {
        return inorganicoService.autoCompletar(input);
    }

    @GetMapping("/autocompletar")
    public InorganicoResultado buscarAutocomplecionInorganico(@RequestParam("autocomplecion") String autocomplecion,
                                                              @RequestParam("pantalla") Short pantalla,
                                                              @RequestParam("premium") Boolean premium) {
        ContextoCliente contexto = new ContextoCliente(pantalla, premium);
        return inorganicoService.buscarAutocomplecion(autocomplecion, contexto);
    }

    // ADMIN --------------------------------------------------------------------------

    @GetMapping("/{id}")
    public Optional<InorganicoModel> seleccionarInorganico(@PathVariable("id") Integer id) {
        return inorganicoService.seleccionar(id);
    }

    @PutMapping()
    public Optional<InorganicoModel> reemplazarInorganico(@RequestBody InorganicoModel nuevo) {
        return inorganicoService.reemplazar(nuevo);
    }

    @PutMapping("/hacerpremium")
    public Optional<InorganicoModel> hacerPremiumInorganico(@RequestParam Integer id) {
        return inorganicoService.hacerPremium(id);
    }

    @PostMapping()
    public InorganicoModel insertarInorganico(@RequestBody InorganicoModel nuevo) {
        return inorganicoService.insertar(nuevo);
    }

    @DeleteMapping()
    public Optional<InorganicoModel> eliminarInorganico(@RequestParam("id") Integer id) {
        return inorganicoService.eliminar(id);
    }

    @GetMapping("/probarfq") // TEST
    public Optional<InorganicoModel> probarPaginaFQ(@RequestParam("direccion") String direccion) {
        return inorganicoService.probarPaginaFQ(direccion);
    }

}
