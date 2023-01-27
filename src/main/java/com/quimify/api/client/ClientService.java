package com.quimify.api.client;

import com.quimify.api.metrics.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// This class implements the logic behind HTTP methods in "/access-data".

@Service
public
class ClientService {

	@Autowired
	ClientRepository clientRepository; // DB connection

	@Autowired
    MetricsService metricsService; // Daily metrics logic

	// Constants:

	public static final short androidPlatform = 0;
	public static final short iOSPlatform = 1;
	public static final short webPlatform = 2;

	// Client:

	protected ClientResult getAccessData(Integer version, Short platform) {
		ClientModel client = clientRepository.findByVersion(version);

		ClientResult clientResult = platform != webPlatform
			? new ClientResult(
				client.getUpdateAvailable(),
				client.getUpdateNeeded(),
				client.getUpdateDetails(),
				client.getMessagePresent(),
				client.getMessageTitle(),
				client.getMessageDetails(),
				client.getMessageLinkPresent(),
				client.getMessageLinkLabel(),
				client.getMessageLink())
			: new ClientResult(
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

		metricsService.countAccess(platform);

		return clientResult;
	}

}
