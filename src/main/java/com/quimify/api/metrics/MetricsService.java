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
public class MetricsService {

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
        return getTodayMetrics().getGoogleQueries();
    }

    public Integer getFreeBingQueries() {
        return getTodayMetrics().getFreeBingQueries();
    }

    // Incrementers:

    @Transactional
    public void errorOccurred() {
        getTodayMetrics().incrementErrors();
    }

    @Transactional
    public void clientErrorSent() {
        getTodayMetrics().incrementClientErrors();
    }

    @Transactional
    public void reportSent() {
        getTodayMetrics().incrementReports();
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
        MetricsModel todayMetrics = getTodayMetrics();

        if(!found)
            todayMetrics.incrementInorganicNotFoundSearches();

        todayMetrics.incrementInorganicSearches();
    }

    @Transactional
    public void freeBingQueried(boolean found) {
        MetricsModel todayMetrics = getTodayMetrics();

        if(!found)
            todayMetrics.incrementFreeBingNotFoundQueries();

        todayMetrics.incrementFreeBingQueries();
    }

    @Transactional
    public void googleQueried(boolean found) {
        MetricsModel todayMetrics = getTodayMetrics();

        if(!found)
            todayMetrics.incrementGoogleNotFoundQueries();

        todayMetrics.incrementGoogleQueries();
    }

    @Transactional
    public void inorganicLearned() {
        getTodayMetrics().incrementInorganicLearnings();
    }

    @Transactional
    public void inorganicCompleted() {
        getTodayMetrics().incrementInorganicCompletions();
    }

    @Transactional
    public void organicFromNameQueried(boolean succeeded) {
        MetricsModel todayMetrics = getTodayMetrics();

        if(!succeeded)
            todayMetrics.incrementOrganicFromNameFailedQueries();

        todayMetrics.incrementOrganicFromNameQueries();
    }

    @Transactional
    public void organicFromStructureQueried(boolean succeeded) {
        MetricsModel todayMetrics = getTodayMetrics();

        if(!succeeded)
            todayMetrics.incrementOrganicFromStructureFailedQueries();

        todayMetrics.incrementOrganicFromStructureQueries();
    }

    @Transactional
    public void molecularMassQueried(boolean succeeded) {
        MetricsModel todayMetrics = getTodayMetrics();

        if(!succeeded)
            todayMetrics.incrementMolecularMassFailedQueries();

        todayMetrics.incrementMolecularMassQueries();
    }

}
