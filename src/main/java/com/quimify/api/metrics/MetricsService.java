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

    private MetricsModel getTodayMetrics() { // TODO fix concurrency issues
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
        return todayMetrics.getGoogleFound() + todayMetrics.getGoogleNotFound();
    }

    public Integer getFreeBingQueries() {
        MetricsModel todayMetrics = getTodayMetrics();
        return todayMetrics.getFreeBingFound() + todayMetrics.getFreeBingNotFound();
    }

    // Incrementers:

    @Transactional
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
        if (platform == AccessDataService.androidPlatform)
            getTodayMetrics().incrementAndroidAccesses();
        else if (platform == AccessDataService.iosPlatform)
            getTodayMetrics().incrementIosAccesses();
    }

    @Transactional
    public void inorganicSearched(boolean found) {
        if (found)
            getTodayMetrics().incrementInorganicsFound();
        else getTodayMetrics().incrementInorganicsNotFound();
    }

    @Transactional
    public void freeBingSearched(boolean found) {
        if(found)
            getTodayMetrics().incrementFreeBingFound();
        else getTodayMetrics().incrementFreeBingNotFound();
    }

    @Transactional
    public void googleSearched(boolean found) {
        if(found)
            getTodayMetrics().incrementGoogleFound();
        else getTodayMetrics().incrementGoogleNotFound();
    }

    @Transactional
    public void inorganicLearned() {
        getTodayMetrics().incrementInorganicsLearned();
    }

    @Transactional
    public void inorganicCompleted() {
        getTodayMetrics().incrementInorganicsCompleted();
    }

    @Transactional
    public void organicFromNameSearched(boolean found) {
        if (found)
            getTodayMetrics().incrementOrganicsFoundFromName();
        else getTodayMetrics().incrementOrganicsNotFoundFromName();
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
