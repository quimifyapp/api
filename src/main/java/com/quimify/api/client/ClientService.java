package com.quimify.api.client;

import com.quimify.api.error.ErrorService;
import com.quimify.api.metrics.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

// This class implements the logic behind HTTP methods in "/client".

@Service
class ClientService {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    MetricsService metricsService;

    @Autowired
    ErrorService errorService;

    // Constants:

    public static final String androidPlatform = "android";
    public static final String iosPlatform = "ios";

    // Client:

    ClientResult get(String platform, Integer version) {
        Optional<ClientModel> client = clientRepository.findByPlatformAndVersion(platform, version);

        if (client.isPresent()) {
            if (platform.equals(androidPlatform))
                metricsService.androidClientAccessed();
            else if (platform.equals(iosPlatform))
                metricsService.iosClientAccessed();

            return new ClientResult(client.get());
        }

        errorService.log("Client not found", "Platform: \"" + platform + "\". Version: " + version, getClass());

        return ClientResult.notFound();
    }

}
