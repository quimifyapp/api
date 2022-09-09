package com.quimify.api.organico;

import com.quimify.api.organico.tipos.Eter;
import com.quimify.api.organico.tipos.Simple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.quimify.api.autorizacion.Autorizacion;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Esta clase implementa los métodos HTTP de la dirección "/organico".

@RestController
@RequestMapping("/organico")
public class OrganicoController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	OrganicoService organicoService; // Procesos de los compuestos orgánicos

	// CLIENTE ------------------------------------------------------------------------

	@GetMapping("/formular")
	public OrganicoResultado formular(@RequestParam("nombre") String nombre,
									  @RequestParam("foto") Boolean foto,
									  @RequestHeader(HttpHeaders.AUTHORIZATION) String clave) {
		if(Autorizacion.esClavePublica(clave))
			return organicoService.formular(nombre, foto);
		else {
			logger.error("Clave pública errónea: \"" + clave + "\".");
			return null;
		}
	}

	@GetMapping("/nombrar/simple")
	public OrganicoResultado nombrarSimple(@RequestParam("secuencia") List<Integer> secuencia,
										   @RequestHeader(HttpHeaders.AUTHORIZATION) String clave) {
		if(!Autorizacion.esClavePublica(clave)) {
			logger.error("Clave pública errónea: \"" + clave + "\".");
			return null;
		}

		Simple generado = new Simple(secuencia);
		return organicoService.nombrar(generado);
	}

	@GetMapping("/nombrar/eter")
	public OrganicoResultado nombrarEter(@RequestParam("secuencia") List<Integer> secuencia,
										 @RequestHeader(HttpHeaders.AUTHORIZATION) String clave) {
		if(!Autorizacion.esClavePublica(clave)) {
			logger.error("Clave pública errónea: \"" + clave + "\".");
			return null;
		}

		Eter generado = new Eter(secuencia);
		return organicoService.nombrar(generado);
	}

}
