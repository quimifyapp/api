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
    private Integer errorsOccurred = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer clientErrorsSent = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer reportsSent = 0;

    // Their sum is equivalent to the total number of client accesses:
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer androidAccesses = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer iosAccesses = 0;

    // Their sum is equivalent to the total number of client inorganics searched:
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicsFound = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicsNotFound = 0;

    // Their sum is equivalent to the total number of client inorganic searches that required a web search:
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer freeBingFound = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer freeBingNotFound = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer googleFound = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer googleNotFound = 0;

    // New inorganics from the web incorporated to the DB:
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicsLearned = 0;

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicsCompleted = 0;

    // Their sum is equivalent to the total number of client organics queried from name:
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer organicsFoundFromName = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer organicsNotFoundFromName = 0;

    // Their sum is equivalent to the total number of client organics queried from structure:
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer organicsSucceededFromStructure = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer organicsFailedFromStructure = 0;

    // Their sum is equivalent to the total number of client molecular masses queried:
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer molecularMassesSucceeded = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer molecularMassesFailed = 0;

    // Constructors:

    MetricsModel(Date date) {
        setDate(date);
    }

    protected MetricsModel() {} // JPA needs it

    // Incrementers:

    void incrementErrorsOccurred() {
        errorsOccurred += 1;
    }

    void incrementClientErrorsSent() {
        clientErrorsSent += 1;
    }

    void incrementReportsSent() {
        reportsSent += 1;
    }

    void incrementAndroidAccesses() {
        androidAccesses += 1;
    }

    void incrementIosAccesses() {
        iosAccesses += 1;
    }

    void incrementInorganicsFound() {
        inorganicsFound += 1;
    }

    void incrementInorganicsNotFound() {
        inorganicsNotFound += 1;
    }

    void incrementFreeBingFound() {
        freeBingFound += 1;
    }

    void incrementFreeBingNotFound() {
        freeBingFound += 1;
    }

    void incrementGoogleFound() {
        googleFound += 1;
    }

    void incrementGoogleNotFound() {
        googleNotFound += 1;
    }

    void incrementInorganicsLearned() {
        inorganicsLearned += 1;
    }

    void incrementInorganicsCompleted() {
        inorganicsCompleted += 1;
    }

    void incrementOrganicsFoundFromName() {
        organicsFoundFromName += 1;
    }

    void incrementOrganicsNotFoundFromName() {
        organicsNotFoundFromName += 1;
    }

    void incrementOrganicsSucceededFromStructure() {
        organicsSucceededFromStructure += 1;
    }

    void incrementOrganicsFailedFromStructure() {
        organicsFailedFromStructure += 1;
    }

    void incrementMolecularMassesSucceeded() {
        molecularMassesSucceeded += 1;
    }

    void incrementMolecularMassesFailed() {
        molecularMassesFailed += 1;
    }

    // Getters and setters:

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getErrorsOccurred() {
        return errorsOccurred;
    }

    public void setErrorsOccurred(Integer errorsOccurred) {
        this.errorsOccurred = errorsOccurred;
    }

    public Integer getClientErrorsSent() {
        return clientErrorsSent;
    }

    public void setClientErrorsSent(Integer clientErrorsSent) {
        this.clientErrorsSent = clientErrorsSent;
    }

    public Integer getReportsSent() {
        return reportsSent;
    }

    public void setReportsSent(Integer reportsSent) {
        this.reportsSent = reportsSent;
    }

    public Integer getAndroidAccesses() {
        return androidAccesses;
    }

    public void setAndroidAccesses(Integer androidAccesses) {
        this.androidAccesses = androidAccesses;
    }

    public Integer getIosAccesses() {
        return iosAccesses;
    }

    public void setIosAccesses(Integer iosAccesses) {
        this.iosAccesses = iosAccesses;
    }

    public Integer getInorganicsFound() {
        return inorganicsFound;
    }

    public void setInorganicsFound(Integer inorganicsFound) {
        this.inorganicsFound = inorganicsFound;
    }

    public Integer getInorganicsNotFound() {
        return inorganicsNotFound;
    }

    public void setInorganicsNotFound(Integer inorganicsNotFound) {
        this.inorganicsNotFound = inorganicsNotFound;
    }

    public Integer getFreeBingFound() {
        return freeBingFound;
    }

    public void setFreeBingFound(Integer freeBingFound) {
        this.freeBingFound = freeBingFound;
    }

    public Integer getFreeBingNotFound() {
        return freeBingNotFound;
    }

    public void setFreeBingNotFound(Integer freeBingNotFound) {
        this.freeBingNotFound = freeBingNotFound;
    }

    public Integer getGoogleFound() {
        return googleFound;
    }

    public void setGoogleFound(Integer googleFound) {
        this.googleFound = googleFound;
    }

    public Integer getGoogleNotFound() {
        return googleNotFound;
    }

    public void setGoogleNotFound(Integer googleNotFound) {
        this.googleNotFound = googleNotFound;
    }

    public Integer getInorganicsLearned() {
        return inorganicsLearned;
    }

    public void setInorganicsLearned(Integer inorganicsLearned) {
        this.inorganicsLearned = inorganicsLearned;
    }

    public Integer getInorganicsCompleted() {
        return inorganicsCompleted;
    }

    public void setInorganicsCompleted(Integer inorganicsCompleted) {
        this.inorganicsCompleted = inorganicsCompleted;
    }

    public Integer getOrganicsFoundFromName() {
        return organicsFoundFromName;
    }

    public void setOrganicsFoundFromName(Integer organicsFoundFromName) {
        this.organicsFoundFromName = organicsFoundFromName;
    }

    public Integer getOrganicsNotFoundFromName() {
        return organicsNotFoundFromName;
    }

    public void setOrganicsNotFoundFromName(Integer organicsNotFoundFromName) {
        this.organicsNotFoundFromName = organicsNotFoundFromName;
    }

    public Integer getOrganicsSucceededFromStructure() {
        return organicsSucceededFromStructure;
    }

    public void setOrganicsSucceededFromStructure(Integer organicsSucceededFromStructure) {
        this.organicsSucceededFromStructure = organicsSucceededFromStructure;
    }

    public Integer getOrganicsFailedFromStructure() {
        return organicsFailedFromStructure;
    }

    public void setOrganicsFailedFromStructure(Integer organicsFailedFromStructure) {
        this.organicsFailedFromStructure = organicsFailedFromStructure;
    }

    public Integer getMolecularMassesSucceeded() {
        return molecularMassesSucceeded;
    }

    public void setMolecularMassesSucceeded(Integer molecularMassesSucceeded) {
        this.molecularMassesSucceeded = molecularMassesSucceeded;
    }

    public Integer getMolecularMassesFailed() {
        return molecularMassesFailed;
    }

    public void setMolecularMassesFailed(Integer molecularMassesFailed) {
        this.molecularMassesFailed = molecularMassesFailed;
    }

}
