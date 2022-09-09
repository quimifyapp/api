package com.quimify.api.cliente;

import com.quimify.api.autorizacion.Autorizacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

// Esta clase implementa los métodos HTTP de la dirección "/cliente".

@RestController
@RequestMapping("/cliente")
public class ClienteController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ClienteService clienteService; // Procesos de los clientes

	// ADMIN --------------------------------------------------------------------------

	@GetMapping()
	public ClienteResultado acceso(@RequestParam("version") Integer version,
								   @RequestParam("plataforma") Short plataforma,
								   @RequestHeader(HttpHeaders.AUTHORIZATION) String clave) {
		if(Autorizacion.esClavePublica(clave))
			return clienteService.acceso(version, plataforma);
		else {
			logger.error("Clave pública errónea: \"" + clave + "\".");
			return null;
		}
	}

}
