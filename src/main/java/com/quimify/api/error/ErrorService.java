package com.quimify.api.error;

import com.quimify.api.metrics.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

// This class implements API errors logic.

@Service
public
class ErrorService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ErrorRepository errorRepository; // DB connection

    @Autowired
    MetricsService metricsService; // Daily metrics logic

    // Internal:

    public void saveError(String title, String details, Class<?> location) {
        ErrorModel errorModel = new ErrorModel();

        errorModel.setDateAndTime(new Timestamp(System.currentTimeMillis()));
        errorModel.setTitle(title);
        errorModel.setDetails(details);
        errorModel.setLocation(location.getName());

        try {
            errorRepository.save(errorModel);
            LoggerFactory.getLogger(location).error(title + ". Details saved in database.");
        } catch (Exception exception) {
            logger.error("Exception saving error in DB. " + "Location: " + location + ". " +
                    "Exception: \"" + exception + "\". " + "Title: \"" + title + "\". " +
                    "Details: \"" + details + "\". ");
        }

        metricsService.countErrorOccurred();
    }

}
