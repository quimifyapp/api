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
        MetricsModel todayMetrics = getTodayMetrics();

        return todayMetrics.getGoogleFoundSearches() + todayMetrics.getGoogleFailedSearches();
    }

    public Integer getBingQueries() {
        MetricsModel todayMetrics = getTodayMetrics();

        return todayMetrics.getBingFoundSearches() + todayMetrics.getBingFailedSearches();
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
    public void inorganicCompleted() {
        getTodayMetrics().incrementInorganicCompletions();
    }

    @Transactional
    public void inorganicSearched(boolean found) {
        MetricsModel todayMetrics = getTodayMetrics();

        if (found)
            todayMetrics.incrementInorganicFoundSearches();
        else todayMetrics.incrementInorganicFailedSearches();
    }

    @Transactional
    public void inorganicClassificationSearched(boolean found) {
        MetricsModel todayMetrics = getTodayMetrics();

        if (found)
            todayMetrics.incrementInorganicFoundClassifications();
        else todayMetrics.incrementInorganicFailedClassifications();
    }

    @Transactional
    public void inorganicCorrectionSearched(boolean found) {
        MetricsModel todayMetrics = getTodayMetrics();

        if (found)
            todayMetrics.incrementInorganicFoundCorrectionSearches();
        else todayMetrics.incrementInorganicFailedCorrectionSearches();
    }

    @Transactional
    public void inorganicSimilaritySearched(boolean found) {
        MetricsModel todayMetrics = getTodayMetrics();

        if (found)
            todayMetrics.incrementInorganicFoundSimilaritySearches();
        else todayMetrics.incrementInorganicFailedSimilaritySearches();
    }

    @Transactional
    public void inorganicDeepSearchFound() {
        getTodayMetrics().incrementInorganicFoundDeepSearches();
    }

    @Transactional
    public void inorganicDeepSearchLearned() {
        getTodayMetrics().incrementInorganicLearnedDeepSearches();
    }

    @Transactional
    public void inorganicDeepSearchFailed() {
        getTodayMetrics().incrementInorganicFailedDeepSearches();
    }

    @Transactional
    public void bingQueried(boolean found) {
        MetricsModel todayMetrics = getTodayMetrics();

        if (found)
            todayMetrics.incrementBingFoundSearches();
        else todayMetrics.incrementBingFailedSearches();
    }

    @Transactional
    public void googleQueried(boolean found) {
        MetricsModel todayMetrics = getTodayMetrics();

        if (found)
            todayMetrics.incrementGoogleFoundSearches();
        else todayMetrics.incrementGoogleFailedSearches();
    }

    @Transactional
    public void organicFromNameQueried(boolean succeeded) {
        MetricsModel todayMetrics = getTodayMetrics();

        if (succeeded)
            todayMetrics.incrementOrganicFromNameFoundQueries();
        else todayMetrics.incrementOrganicFromNameFailedQueries();
    }

    @Transactional
    public void organicFromStructureQueried(boolean succeeded) {
        MetricsModel todayMetrics = getTodayMetrics();

        if (succeeded)
            todayMetrics.incrementOrganicFromStructureFoundQueries();
        else todayMetrics.incrementOrganicFromStructureFailedQueries();
    }

    @Transactional
    public void molecularMassQueried(boolean succeeded) {
        MetricsModel todayMetrics = getTodayMetrics();

        if (succeeded)
            todayMetrics.incrementMolecularMassFoundQueries();
        else todayMetrics.incrementMolecularMassFailedQueries();
    }

}
