package com.quimify.api.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// Esta clase implementa los métodos HTTP de la dirección "/cliente".

@RestController
@RequestMapping("/client")
class ClientController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ClientService clienteService; // Procesos de los clientes

	// Constants:

	private static final String getAccessMessage = "GET access data from %s V%s client.";

	// Client:

	@GetMapping("access-data")
	protected ClientResult getAccessData(@RequestParam("version") Integer version,
										 @RequestParam("platform") Short platform) {
		logger.info(String.format(getAccessMessage, platform == 0 ? "Android" : platform == 1 ? "iOS" : "", version));
		return clienteService.getAccessData(version, platform);
	}

}
