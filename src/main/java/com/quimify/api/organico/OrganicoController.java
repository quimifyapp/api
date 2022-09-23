package com.quimify.api.organico;

import com.quimify.api.organico.tipos.Eter;
import com.quimify.api.organico.tipos.Simple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// Esta clase implementa los métodos HTTP de la dirección "/organico".

@RestController
@RequestMapping("/organico")
public class OrganicoController {

	@Autowired
	OrganicoService organicoService; // Procesos de los compuestos orgánicos

	// CLIENTE ------------------------------------------------------------------------

	@GetMapping("/formular")
	public OrganicoResultado formular(@RequestParam("nombre") String nombre,
									  @RequestParam("foto") Boolean foto) {
		return organicoService.formular(nombre, foto);
	}

	@GetMapping("/nombrar/simple")
	public OrganicoResultado nombrarSimple(@RequestParam("secuencia") int[] secuencia) {
		Simple generado = new Simple(secuencia);
		return organicoService.nombrar(generado);
	}

	@GetMapping("/nombrar/eter")
	public OrganicoResultado nombrarEter(@RequestParam("secuencia") int[] secuencia) {
		Eter generado = new Eter(secuencia);
		return organicoService.nombrar(generado);
	}

}
