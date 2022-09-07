package com.quimify.servidor.bienvenida;

import com.quimify.servidor.autentificacion.Autentificacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// Esta clase implementa los métodos HTTP de la dirección "/bienvenida".

@RestController
@RequestMapping("/bienvenida")
public class BienvenidaController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	BienvenidaService bienvenidaService; // Procesos de la bienvenida

	// ADMIN --------------------------------------------------------------------------

	@GetMapping()
	public BienvenidaResultado bienvenida(@RequestParam("plataforma") Short plataforma,
										  @RequestParam("clave") String clave) {
		if(Autentificacion.esClavePublica(clave))
			return bienvenidaService.bienvenida(plataforma);
		else {
			logger.error("Clave pública errónea: \"" + clave + "\".");
			return null;
		}
	}

}
