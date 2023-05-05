package com.quimify.api.accessdata;

import com.quimify.api.metrics.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// This class implements the logic behind HTTP methods in "/access-data".

@Service
public
class AccessDataService {

	@Autowired
	AccessDataRepository accessDataRepository; // DB connection

	@Autowired
    MetricsService metricsService; // Daily metrics logic

	// Constants:

	public static final short androidPlatform = 0;
	public static final short iOSPlatform = 1;
	public static final short webPlatform = 2;

	// Client:

	AccessDataResult getAccessData(Integer clientVersion, Short platform) {
		AccessDataModel client = accessDataRepository.findByClientVersion(clientVersion);

		AccessDataResult accessDataResult = platform != webPlatform
			? new AccessDataResult(
				client.getUpdateAvailable(),
				client.getUpdateNeeded(),
				client.getUpdateDetails(),
				client.getMessagePresent(),
				client.getMessageTitle(),
				client.getMessageDetails(),
				client.getMessageLinkPresent(),
				client.getMessageLinkLabel(),
				client.getMessageLink())
			: new AccessDataResult(
				false,
				null,
				null,
				client.getMessagePresent(),
				client.getMessageTitle(),
				client.getMessageDetails(),
				client.getMessageLinkPresent(),
				client.getMessageLinkLabel(),
				client.getMessageLink()
		);

		metricsService.clientAccessed(platform);

		return accessDataResult;
	}

}
