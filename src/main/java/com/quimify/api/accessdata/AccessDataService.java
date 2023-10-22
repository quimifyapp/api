package com.quimify.api.accessdata;

import com.quimify.api.metrics.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// This class implements the logic behind HTTP methods in "/access-data".

@Service
public class AccessDataService {

    @Autowired
    AccessDataRepository accessDataRepository; 

    @Autowired
    MetricsService metricsService;

    // Constants:

    public static final short androidPlatform = 0;
    public static final short iosPlatform = 1;

    // Client:

    AccessDataResult getAccessData(Integer clientVersion, Short platform) {
        AccessDataModel client = accessDataRepository.findByClientVersion(clientVersion);

        AccessDataResult accessDataResult = new AccessDataResult(
                client.getUpdateAvailable(),
                client.getUpdateNeeded(),
                client.getUpdateDetails(),
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
