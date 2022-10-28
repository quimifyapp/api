package com.quimify.api.organic;

import com.quimify.organic.OrganicResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// Esta clase implementa los métodos HTTP de la dirección "/organic".

@RestController
@RequestMapping("/organic")
class OrganicController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	OrganicService organicService; // Procesos de los compuestos orgánicos

	// CLIENTE ------------------------------------------------------------------------

	@GetMapping("from-name")
	protected OrganicResult name(@RequestParam("name") String name,
							  @RequestParam("picture") Boolean picture) {
		OrganicResult organicResult = organicService.getFromName(name, picture);

		if (organicResult.getPresent())
			logger.info("GET formular: \"" + name + "\". RETURN: " + organicResult.getStructure() + ".");

		return organicResult;
	}

	@GetMapping("from-structure")
	protected OrganicResult structure(@RequestParam("structure-sequence") int[] structureSequence) {
		OrganicResult organicResult = organicService.getFromStructure(structureSequence);

		logger.info("GET nombrar: \"" + organicResult.getStructure() + "\". " +
				"RETURN: \"" + organicResult.getName() + "\".");

		return organicResult;
	}

}
