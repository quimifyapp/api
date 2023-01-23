package com.quimify.api.organic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// This class implements HTTP methods in "/organic".

@RestController
@RequestMapping("/organic")
class OrganicController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	OrganicService organicService; // Procesos de los compuestos orgánicos

	// Client:

	@GetMapping("from-name")
	protected OrganicResult name(@RequestParam("name") String name, @RequestParam("picture") Boolean picture) {
		OrganicResult result = organicService.getFromName(name, picture);

		if (result.isPresent())
			logger.info("GET organic: \"" + name + "\". " + "RETURN: " + result.getStructure() + ".");

		return result;
	}

	@GetMapping("from-structure")
	protected OrganicResult structure(@RequestParam("structure-sequence") int[] structureSequence) {
		OrganicResult result = organicService.getFromStructure(structureSequence);

		logger.info("GET organic: \"" + result.getStructure() + "\". " + "RETURN: \"" + result.getName() + "\".");

		return result;
	}

}
