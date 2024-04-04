package com.quimify.api.metrics;

// This class represents a day's worth of metrics.

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "metrics")
class MetricsModel {

    @Id
    @Temporal(TemporalType.DATE)
    private Date date;

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer errors = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer clientErrors = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer reports = 0;

    // Their sum is equivalent to the total number of client accesses:
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer androidAccesses = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer iosAccesses = 0;

    // Their sum is the total of inorganics queried:
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicFoundSearches = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicFailedSearches = 0;

    // Their sum is the total of classifications attempted:
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicFoundClassifications = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicFailedClassifications = 0;

    // Their sum minus failed classifications is the total of rejected classifications:
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicFoundCorrectionSearches = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicFailedCorrectionSearches = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicFoundSimilaritySearches = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicFailedSimilaritySearches = 0;

    // Their sum minus failed corrections and failed similarities is the total of rejected smart searches:
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicFoundDeepSearches = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicLearnedDeepSearches = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicFailedDeepSearches = 0;

    // Their sum is the total of web searches done:
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer bingFoundSearches = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer bingFailedSearches = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer googleFoundSearches = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer googleFailedSearches = 0;

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicCompletions = 0;

    // Their sum is the total of client organics queried from name:
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer organicFromNameFoundQueries = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer organicFromNameFailedQueries = 0;

    // Their sum is the total of client organics queried from structure:
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer organicFromStructureFoundQueries = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer organicFromStructureFailedQueries = 0;

    // Their sum is the total of client molecular masses queried:
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer molecularMassFoundQueries = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer molecularMassFailedQueries = 0;

    // Constructors:

    MetricsModel(Date date) {
        this.date = date;
    }

    protected MetricsModel() {} // Needed by JPA, don't touch

    // Incrementers:

    void incrementErrors() {
        errors += 1;
    }

    void incrementClientErrors() {
        clientErrors += 1;
    }

    void incrementReports() {
        reports += 1;
    }

    void incrementAndroidAccesses() {
        androidAccesses += 1;
    }

    void incrementIosAccesses() {
        iosAccesses += 1;
    }

    void incrementInorganicCompletions() {
        inorganicCompletions += 1;
    }

    void incrementInorganicFoundSearches() {
        inorganicFoundSearches += 1;
    }

    void incrementInorganicFailedSearches() {
        inorganicFailedSearches += 1;
    }

    void incrementInorganicFoundClassifications() {
        inorganicFoundClassifications += 1;
    }

    void incrementInorganicFailedClassifications() {
        inorganicFailedClassifications += 1;
    }

    void incrementInorganicFoundCorrectionSearches() {
        inorganicFoundCorrectionSearches += 1;
    }

    void incrementInorganicFailedCorrectionSearches() {
        inorganicFailedCorrectionSearches += 1;
    }

    void incrementInorganicFoundSimilaritySearches() {
        inorganicFoundSimilaritySearches += 1;
    }

    void incrementInorganicFailedSimilaritySearches() {
        inorganicFailedSimilaritySearches += 1;
    }

    void incrementInorganicFoundDeepSearches() {
        inorganicFoundDeepSearches += 1;
    }

    void incrementInorganicLearnedDeepSearches() {
        inorganicLearnedDeepSearches += 1;
    }

    void incrementInorganicFailedDeepSearches() {
        inorganicFailedDeepSearches += 1;
    }

    void incrementBingFoundSearches() {
        bingFoundSearches += 1;
    }

    void incrementBingFailedSearches() {
        bingFailedSearches += 1;
    }

    void incrementGoogleFoundSearches() {
        googleFoundSearches += 1;
    }

    void incrementGoogleFailedSearches() {
        googleFailedSearches += 1;
    }

    void incrementOrganicFromNameFoundQueries() {
        organicFromNameFoundQueries += 1;
    }

    void incrementOrganicFromNameFailedQueries() {
        organicFromNameFailedQueries += 1;
    }

    void incrementOrganicFromStructureFoundQueries() {
        organicFromStructureFoundQueries += 1;
    }

    void incrementOrganicFromStructureFailedQueries() {
        organicFromStructureFailedQueries += 1;
    }

    void incrementMolecularMassFoundQueries() {
        molecularMassFoundQueries += 1;
    }

    void incrementMolecularMassFailedQueries() {
        molecularMassFailedQueries += 1;
    }

    // Getters and setters:


    Integer getBingFoundSearches() {
        return bingFoundSearches;
    }

    Integer getBingFailedSearches() {
        return bingFailedSearches;
    }

    Integer getGoogleFoundSearches() {
        return googleFoundSearches;
    }

    Integer getGoogleFailedSearches() {
        return googleFailedSearches;
    }

}
