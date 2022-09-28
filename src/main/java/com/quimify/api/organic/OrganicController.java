package com.quimify.api.organic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// Esta clase implementa los métodos HTTP de la dirección "/organic".

@RestController
@RequestMapping("/organic")
public class OrganicController {

	@Autowired
	OrganicService organicService; // Procesos de los compuestos orgánicos

	// CLIENTE ------------------------------------------------------------------------

	@GetMapping("/name")
	public OrganicResult name(@RequestParam("name") String name,
							  @RequestParam("picture") Boolean picture) {
		return organicService.getFromName(name, picture);
	}

	@GetMapping("/structure")
	public OrganicResult structure(@RequestParam("input-sequence") int[] inputSequence) {
		return organicService.getFromStructure(inputSequence);
	}

}
