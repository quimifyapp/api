package com.quimify.api.error;

import com.quimify.api.metrics.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

// This class implements API errors logic.

@Service
public class ErrorService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ErrorRepository errorRepository;

    @Autowired
    MetricsService metricsService;

    // Protected:

    public void log(String title, String details, Class<?> location) {
        ErrorModel errorModel = new ErrorModel();

        String locationName = location.getName().replaceAll(".*\\.", "");

        errorModel.setDateAndTime(Timestamp.from(Instant.now()));
        errorModel.setTitle(title);
        errorModel.setDetails(details);
        errorModel.setLocation(locationName);

        try {
            errorRepository.save(errorModel);
            LoggerFactory.getLogger(location).error("{}. Details saved in database.", title);
        } catch (Exception exception) {
            logger.error("Exception saving error in DB. Location: {}. Exception: \"{}\". Title: \"{}\". " +
                    "Details: \"{}\". ", locationName, exception, title, details);
        }

        metricsService.errorOccurred();
    }

}
