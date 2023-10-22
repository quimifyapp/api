package com.quimify.api.organic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// This class implements HTTP methods in "/organic".

@RestController
@RequestMapping("/organic")
class OrganicController {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	OrganicService organicService;

	// Constants:

	private static final String getOrganicMessage = "GET organic %s: \"%s\". RESULT: %s.";

	// Client:

	@GetMapping("/structure")
	OrganicResult getFromName(@RequestParam("name") String name) {
		OrganicResult result = organicService.getFromName(name);

		if (result.isFound())
			logger.info(String.format(getOrganicMessage, "name", name, result.getStructure()));

		return result;
	}

	@GetMapping("/name")
	OrganicResult getFromStructure(@RequestParam("structure-sequence") int[] structureSequence) {
		OrganicResult result = organicService.getFromStructure(structureSequence);

		if(result.isFound())
			logger.info(String.format(getOrganicMessage, "structure", result.getStructure(), result.getName()));

		return result;
	}

}
