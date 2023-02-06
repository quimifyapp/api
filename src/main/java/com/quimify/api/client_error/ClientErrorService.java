package com.quimify.api.client_error;

import com.quimify.api.metrics.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

// This class implements the logic behind HTTP methods in "/client-error".

@Service
class ClientErrorService {

    @Autowired
    ClientErrorRepository clientErrorRepository; // DB connection

    @Autowired
    MetricsService metricsService; // Daily metrics logic

    // Client:

    protected void save(String context, String details, Integer clientVersion) {
        ClientErrorModel clientErrorModel = new ClientErrorModel();

        clientErrorModel.setDateAndTime(Timestamp.from(Instant.now()));
        clientErrorModel.setContext(context);
        clientErrorModel.setDetails(details);
        clientErrorModel.setClientVersion(clientVersion);

        clientErrorRepository.save(clientErrorModel);

        metricsService.clientErrorSent();
    }

}
