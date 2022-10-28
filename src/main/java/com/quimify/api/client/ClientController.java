package com.quimify.api.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// Esta clase implementa los métodos HTTP de la dirección "/cliente".

@RestController
@RequestMapping("/client")
public class ClientController {

	@Autowired
	ClientService clienteService; // Procesos de los clientes

	// CLIENTE ------------------------------------------------------------------------

	@GetMapping()
	public ClientResult getAccessData(@RequestParam("version") Integer version,
									  @RequestParam("platform") Short platform) {
		return clienteService.getAccessData(version, platform);
	}

}
