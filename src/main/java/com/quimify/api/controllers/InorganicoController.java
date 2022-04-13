package com.quimify.api.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InorganicoController {

    @GetMapping("/prueba")
    public String compuestoDePrueba() {
        return "óxido de dihidrógeno";
    }

    @GetMapping("/hugo")
    public String hugo() {
        return "Gay";
    }

    @GetMapping("/mateo")
    public String mateo() {
        return "El ateo";
    }

}
