package com.quimify.api.metrics;

import com.quimify.api.accessdata.AccessDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.Date;
import java.util.Optional;

// This class handles daily metrics.

@Service
public
class MetricsService {

    @Autowired
    MetricsRepository metricsRepository; // DB connection

    // Day starts at 13:00 of Spain and 07:00 of Bolivia
    private static final Duration offSet = Duration.ofHours(-13);

    // Private:

    private MetricsModel getTodayMetrics() {
        MetricsModel todayMetrics;

        // Day starts at 13:00 of Spain and 07:00 of Bolivia
        Date today = Date.from(Instant.now().plus(offSet));
        Optional<MetricsModel> latestMetrics = metricsRepository.findById(today);

        if (latestMetrics.isEmpty())
            todayMetrics = metricsRepository.save(new MetricsModel(today));
        else todayMetrics = latestMetrics.get();

        return todayMetrics;
    }

    // Queries:

    public Integer getGoogleQueries() {
        MetricsModel todayMetrics = getTodayMetrics();

        return todayMetrics.getGoogleFoundFromText() + todayMetrics.getGoogleNotFoundFromText();
    }

    public Integer getPaidBingQueries() {
        return getTodayMetrics().getPaidBingQueries();
    }

    // Incrementers:

    @Transactional // TODO Transactional here?
    public void errorOccurred() {
        getTodayMetrics().incrementErrorsOccurred();
    }

    @Transactional
    public void clientErrorSent() {
        getTodayMetrics().incrementClientErrorsSent();
    }

    @Transactional
    public void reportSent() {
        getTodayMetrics().incrementReportsSent();
    }

    @Transactional
    public void clientAccessed(Short platform) {
        switch (platform) {
            case AccessDataService.androidPlatform:
                getTodayMetrics().incrementAndroidAccesses();
                break;
            case AccessDataService.iOSPlatform:
                getTodayMetrics().incrementIOSAccesses();
                break;
            case AccessDataService.webPlatform:
                getTodayMetrics().incrementWebAccesses();
                break;
        }
    }

    @Transactional
    public void inorganicSearched(boolean found) {
        if (found)
            getTodayMetrics().incrementInorganicsFoundFromText();
        else getTodayMetrics().incrementInorganicsNotFoundFromText();
    }

    @Transactional
    public void googleSearchFound() {
        getTodayMetrics().incrementGoogleFoundFromText();
    }

    @Transactional
    public void googleSearchNotFound() {
        getTodayMetrics().incrementGoogleNotFoundFromText();
    }

    @Transactional
    public void bingSearchFound() {
        getTodayMetrics().incrementBingFoundFromText();
    }

    @Transactional
    public void bingSearchNotFound() {
        getTodayMetrics().incrementBingNotFoundFromText();
    }

    @Transactional
    public void paidBingQuery() {
        getTodayMetrics().incrementPaidBingQueries();
    }

    @Transactional
    public void inorganicLearned() {
        getTodayMetrics().incrementInorganicsLearned();
    }

    @Transactional
    public void inorganicAutocompleted() { // TODO rename
        getTodayMetrics().incrementInorganicsAutocompleted();
    }

    @Transactional
    public void organicFromNameSearched(boolean found) {
        if (found)
            getTodayMetrics().incrementOrganicsFoundFromNameFromText();
        else getTodayMetrics().incrementOrganicsNotFoundFromNameFromText();
    }

    @Transactional
    public void organicFromStructureSearched(boolean found) {
        if (found)
            getTodayMetrics().incrementOrganicsSucceededFromStructure();
        else getTodayMetrics().incrementOrganicsFailedFromStructure();
    }

    @Transactional
    public void molecularMassSearched(boolean found) {
        if (found)
            getTodayMetrics().incrementMolecularMassesSucceeded();
        else getTodayMetrics().incrementMolecularMassesFailed();
    }

}
