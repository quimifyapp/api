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
	OrganicService organicService; // Organic compounds logic

	// Constants:

	private static final String getOrganicMessage = "GET organic %s: \"%s\". RETURN: %s.";

	// Client:

	@GetMapping("/structure")
	protected OrganicResult getFromName(@RequestParam("name") String name) {
		OrganicResult result = organicService.getFromName(name);

		if (result.isPresent())
			logger.info(String.format(getOrganicMessage, "name", name, result.getStructure()));

		return result;
	}

	@GetMapping("/name")
	protected OrganicResult getFromStructure(@RequestParam("structure-sequence") int[] structureSequence) {
		OrganicResult result = organicService.getFromStructure(structureSequence);

		if(result.isPresent())
			logger.info(String.format(getOrganicMessage, "structure", result.getStructure(), result.getName()));

		return result;
	}

}
