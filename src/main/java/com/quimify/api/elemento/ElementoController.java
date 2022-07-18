package com.quimify.api.elemento;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

// Esta clase implementa los métodos HTTP de la dirección "/elemento".

@RestController
@RequestMapping("/elemento")
public class ElementoController {

    @Autowired
    ElementoService elementoService; // Procesos de los elementos

    // ADMIN --------------------------------------------------------------------------

    @GetMapping("/{id}")
    public Optional<ElementoModel> seleccionarElemento(@PathVariable("id") Integer id) {
        return elementoService.seleccionar(id);
    }

    @PutMapping()
    public Optional<ElementoModel> reemplazarElemento(@RequestBody ElementoModel nuevo) {
        return elementoService.reemplazar(nuevo);
    }

    @PostMapping()
    public ElementoModel insertarElemento(@RequestBody ElementoModel nuevo) {
        return elementoService.insertar(nuevo);
    }

    @DeleteMapping()
    public Optional<ElementoModel> eliminarElemento(@RequestParam("id") Integer id) {
        return elementoService.eliminar(id);
    }

    @GetMapping("/masamolecular") // TEST
    public Float masaMolecular(@RequestParam("formula") String formula) {
        return elementoService.masaMolecular(formula);
    }
}
