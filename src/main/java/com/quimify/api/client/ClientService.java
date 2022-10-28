package com.quimify.api.client;

import com.quimify.api.metrics.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Esta clase procesa la bienvenida.

@Service
public
class ClientService {

	@Autowired
	ClientRepository clientRepository; // Conexión con la DB

	@Autowired
    MetricsService metricsService; // Procesos de las métricas diarias

	public static final short androidPlatform = 0;
	public static final short iOSPlatform = 1;
	public static final short webPlatform = 2;

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

		metricsService.contarAcceso(platform);

		return clientResult;
	}

}
