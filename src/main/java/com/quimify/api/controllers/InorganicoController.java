package com.quimify.api.controllers;

import com.quimify.api.models.InorganicoModel;
import com.quimify.api.results.InorganicoResult;
import com.quimify.api.services.InorganicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

// Esta clase contiene los métodos HTTP.

// Inorganico = (id, busquedas, premium, formula, nombre, alternativo, masa, densidad, fu., eb.)

@RestController
@RequestMapping("/inorganico")
public class InorganicoController {
    @Autowired
    InorganicoService inorganicoService; // Conexión con la DB.

    @GetMapping()
    public InorganicoResult buscar(@RequestParam("input") String input,
                                   @RequestParam("premium") Boolean premium) {
        return inorganicoService.buscar(input, premium);
    }

    @GetMapping("/todos")
    public ArrayList<InorganicoModel> obtenerTodos() {
        return inorganicoService.obtenerTodos();
    }
//ola
    @PostMapping("/guardar")
    public InorganicoModel guardarInorganico(@RequestBody InorganicoModel inorganico) {
        return inorganicoService.guardarInorganico(inorganico);
    }

}
