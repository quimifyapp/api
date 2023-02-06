package com.quimify.api.access_data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// This class implements HTTP methods in "/access-data".

@RestController
@RequestMapping("/access-data")
class AccessDataController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	AccessDataService accessDataService; // Access data logic

	// Constants:

	private static final String getAccessDataMessage = "GET access data from %s V%s client.";

	// Client:

	@GetMapping()
	protected AccessDataResult getAccessData(@RequestParam("client-version") Integer clientVersion,
											 @RequestParam("platform") Short platform) {
		String platformName = platform == 0 ? "Android" : platform == 1 ? "iOS" : "web";
		logger.info(String.format(getAccessDataMessage, platformName, clientVersion));

		return accessDataService.getAccessData(clientVersion, platform);
	}

}