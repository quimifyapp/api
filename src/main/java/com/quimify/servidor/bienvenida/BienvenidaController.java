package com.quimify.servidor.bienvenida;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// Esta clase implementa los métodos HTTP de la dirección "/bienvenida".

@RestController
@RequestMapping("/bienvenida")
public class BienvenidaController {

	@Autowired
	BienvenidaService bienvenidaService; // Procesos de la bienvenida

	// ADMIN --------------------------------------------------------------------------

	@GetMapping()
	public BienvenidaResultado bienvenida() {
		return bienvenidaService.bienvenida();
	}

}
