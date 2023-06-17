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

    // Their sum is equivalent to the total number of client inorganics searched:
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicSearches = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicNotFoundSearches = 0;

    // Their sum is equivalent to the total number of client inorganic searches that required a web search:
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer freeBingQueries = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer freeBingNotFoundQueries = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer googleQueries = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer googleNotFoundQueries = 0;

    // New inorganics from the web incorporated to the DB:
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicLearnings = 0;

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicCompletions = 0;

    // Their sum is equivalent to the total number of client organics queried from name:
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer organicFromNameQueries = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer organicFromNameFailedQueries = 0;

    // Their sum is equivalent to the total number of client organics queried from structure:
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer organicFromStructureQueries = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer organicFromStructureFailedQueries = 0;

    // Their sum is equivalent to the total number of client molecular masses queried:
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer molecularMassQueries = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer molecularMassFailedQueries = 0;

    // Constructors:

    MetricsModel(Date date) {
        setDate(date);
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

    void incrementInorganicSearches() {
        inorganicSearches += 1;
    }

    void incrementInorganicNotFoundSearches() {
        inorganicNotFoundSearches += 1;
    }

    void incrementFreeBingQueries() {
        freeBingQueries += 1;
    }

    void incrementFreeBingNotFoundQueries() {
        freeBingNotFoundQueries += 1;
    }

    void incrementGoogleQueries() {
        googleQueries += 1;
    }

    void incrementGoogleNotFoundQueries() {
        googleNotFoundQueries += 1;
    }

    void incrementInorganicLearnings() {
        inorganicLearnings += 1;
    }

    void incrementInorganicCompletions() {
        inorganicCompletions += 1;
    }

    void incrementOrganicFromNameQueries() {
        organicFromNameQueries += 1;
    }

    void incrementOrganicFromNameFailedQueries() {
        organicFromNameFailedQueries += 1;
    }

    void incrementOrganicFromStructureQueries() {
        organicFromStructureQueries += 1;
    }

    void incrementOrganicFromStructureFailedQueries() {
        organicFromStructureFailedQueries += 1;
    }

    void incrementMolecularMassQueries() {
        molecularMassQueries += 1;
    }

    void incrementMolecularMassFailedQueries() {
        molecularMassFailedQueries += 1;
    }


    // Getters and setters:

    Date getDate() {
        return date;
    }

    void setDate(Date date) {
        this.date = date;
    }

    Integer getErrors() {
        return errors;
    }

    void setErrors(Integer errors) {
        this.errors = errors;
    }

    Integer getClientErrors() {
        return clientErrors;
    }

    void setClientErrors(Integer clientErrors) {
        this.clientErrors = clientErrors;
    }

    Integer getReports() {
        return reports;
    }

    void setReports(Integer reports) {
        this.reports = reports;
    }

    Integer getAndroidAccesses() {
        return androidAccesses;
    }

    void setAndroidAccesses(Integer androidAccesses) {
        this.androidAccesses = androidAccesses;
    }

    Integer getIosAccesses() {
        return iosAccesses;
    }

    void setIosAccesses(Integer iosAccesses) {
        this.iosAccesses = iosAccesses;
    }

    Integer getInorganicSearches() {
        return inorganicSearches;
    }

    void setInorganicSearches(Integer inorganicSearches) {
        this.inorganicSearches = inorganicSearches;
    }

    Integer getInorganicNotFoundSearches() {
        return inorganicNotFoundSearches;
    }

    void setInorganicNotFoundSearches(Integer inorganicNotFoundSearches) {
        this.inorganicNotFoundSearches = inorganicNotFoundSearches;
    }

    Integer getFreeBingQueries() {
        return freeBingQueries;
    }

    void setFreeBingQueries(Integer freeBingQueries) {
        this.freeBingQueries = freeBingQueries;
    }

    Integer getFreeBingNotFoundQueries() {
        return freeBingNotFoundQueries;
    }

    void setFreeBingNotFoundQueries(Integer freeBingNotFoundQueries) {
        this.freeBingNotFoundQueries = freeBingNotFoundQueries;
    }

    Integer getGoogleQueries() {
        return googleQueries;
    }

    void setGoogleQueries(Integer googleQueries) {
        this.googleQueries = googleQueries;
    }

    Integer getGoogleNotFoundQueries() {
        return googleNotFoundQueries;
    }

    void setGoogleNotFoundQueries(Integer googleNotFoundQueries) {
        this.googleNotFoundQueries = googleNotFoundQueries;
    }

    Integer getInorganicLearnings() {
        return inorganicLearnings;
    }

    void setInorganicLearnings(Integer inorganicLearnings) {
        this.inorganicLearnings = inorganicLearnings;
    }

    Integer getInorganicCompletions() {
        return inorganicCompletions;
    }

    void setInorganicCompletions(Integer inorganicCompletions) {
        this.inorganicCompletions = inorganicCompletions;
    }

    Integer getOrganicFromNameQueries() {
        return organicFromNameQueries;
    }

    void setOrganicFromNameQueries(Integer organicFromNameQueries) {
        this.organicFromNameQueries = organicFromNameQueries;
    }

    Integer getOrganicFromNameFailedQueries() {
        return organicFromNameFailedQueries;
    }

    void setOrganicFromNameFailedQueries(Integer organicFromNameFailedQueries) {
        this.organicFromNameFailedQueries = organicFromNameFailedQueries;
    }

    Integer getOrganicFromStructureQueries() {
        return organicFromStructureQueries;
    }

    void setOrganicFromStructureQueries(Integer organicFromStructureQueries) {
        this.organicFromStructureQueries = organicFromStructureQueries;
    }

    Integer getOrganicFromStructureFailedQueries() {
        return organicFromStructureFailedQueries;
    }

    void setOrganicFromStructureFailedQueries(Integer organicFromStructureFailedQueries) {
        this.organicFromStructureFailedQueries = organicFromStructureFailedQueries;
    }

    Integer getMolecularMassQueries() {
        return molecularMassQueries;
    }

    void setMolecularMassQueries(Integer molecularMassQueries) {
        this.molecularMassQueries = molecularMassQueries;
    }

    Integer getMolecularMassFailedQueries() {
        return molecularMassFailedQueries;
    }

    void setMolecularMassFailedQueries(Integer molecularMassFailedQueries) {
        this.molecularMassFailedQueries = molecularMassFailedQueries;
    }

}
