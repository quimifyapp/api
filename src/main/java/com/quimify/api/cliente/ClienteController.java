package com.quimify.api.cliente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// Esta clase implementa los métodos HTTP de la dirección "/cliente".

@RestController
@RequestMapping("/cliente")
public class ClienteController {

	@Autowired
	ClienteService clienteService; // Procesos de los clientes

	// CLIENTE ------------------------------------------------------------------------

	@GetMapping()
	public ClienteResultado acceso(@RequestParam("version") Integer version,
								   @RequestParam("plataforma") Short plataforma) {
		return clienteService.getClient(version, plataforma);
	}

}
