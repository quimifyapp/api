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
public
class ErrorService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ErrorRepository errorRepository; // DB connection

    @Autowired
    MetricsService metricsService; // Daily metrics logic

    // Protected:

    public void saveError(String title, String details, Class<?> location) {
        ErrorModel errorModel = new ErrorModel();

        String locationName = location.getName().replaceAll(".*\\.", "");

        errorModel.setDateAndTime(Timestamp.from(Instant.now()));
        errorModel.setTitle(title);
        errorModel.setDetails(details);
        errorModel.setLocation(locationName);

        try {
            errorRepository.save(errorModel);
            LoggerFactory.getLogger(location).error(title + ". Details saved in database.");
        } catch (Exception exception) {
            logger.error("Exception saving error in DB. " + "Location: " + locationName + ". " + "Exception: \"" +
                    exception + "\". " + "Title: \"" + title + "\". " + "Details: \"" + details + "\". ");
        }

        metricsService.countErrorOccurred();
    }

}
