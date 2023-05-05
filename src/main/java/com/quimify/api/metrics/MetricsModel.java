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
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer webAccesses = 0;

    // Their sum is equivalent to the total number of client inorganics searched:
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicsFoundFromText = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicsNotFoundFromText = 0;

    // Their sum is equivalent to the total number of client inorganic searches that required a web search:
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer googleFoundFromText = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer googleNotFoundFromText = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer bingFoundFromText = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer bingNotFoundFromText = 0;

    // Paid Bing queries are recorded explicitly so a daily limit can be set:
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer paidBingQueries = 0;

    // New inorganics from the web incorporated to the DB:
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicsLearned = 0;

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicsAutocompleted = 0;

    // Their sum is equivalent to the total number of client organics queried from name:
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer organicsFoundFromNameFromText = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer organicsNotFoundFromNameFromText = 0;

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

    void incrementIOSAccesses() {
        iosAccesses += 1;
    }

    void incrementWebAccesses() {
        webAccesses += 1;
    }

    void incrementInorganicsFoundFromText() {
        inorganicsFoundFromText += 1;
    }

    void incrementInorganicsNotFoundFromText() {
        inorganicsNotFoundFromText += 1;
    }

    void incrementGoogleFoundFromText() {
        googleFoundFromText += 1;
    }

    void incrementGoogleNotFoundFromText() {
        googleNotFoundFromText += 1;
    }

    void incrementBingFoundFromText() {
        bingFoundFromText += 1;
    }

    void incrementBingNotFoundFromText() {
        bingNotFoundFromText += 1;
    }

    void incrementPaidBingQueries() {
        paidBingQueries += 1;
    }

    void incrementInorganicsLearned() {
        inorganicsLearned += 1;
    }

    void incrementInorganicsAutocompleted() {
        inorganicsAutocompleted += 1;
    }

    void incrementOrganicsFoundFromNameFromText() {
        organicsFoundFromNameFromText += 1;
    }

    void incrementOrganicsNotFoundFromNameFromText() {
        organicsNotFoundFromNameFromText += 1;
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

    Date getDate() {
        return date;
    }

    void setDate(Date date) {
        this.date = date;
    }

    Integer getErrorsOccurred() {
        return errorsOccurred;
    }

    void setErrorsOccurred(Integer errorsOccurred) {
        this.errorsOccurred = errorsOccurred;
    }

    Integer getClientErrorsSent() {
        return clientErrorsSent;
    }

    void setClientErrorsSent(Integer clientErrorsOccurred) {
        this.clientErrorsSent = clientErrorsOccurred;
    }

    Integer getReportsSent() {
        return reportsSent;
    }

    void setReportsSent(Integer reportsSent) {
        this.reportsSent = reportsSent;
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

    Integer getWebAccesses() {
        return webAccesses;
    }

    void setWebAccesses(Integer webAccesses) {
        this.webAccesses = webAccesses;
    }

    Integer getInorganicsFoundFromText() {
        return inorganicsFoundFromText;
    }

    void setInorganicsFoundFromText(Integer inorganicsFoundFromText) {
        this.inorganicsFoundFromText = inorganicsFoundFromText;
    }

    Integer getInorganicsNotFoundFromText() {
        return inorganicsNotFoundFromText;
    }

    void setInorganicsNotFoundFromText(Integer inorganicsNotFoundFromText) {
        this.inorganicsNotFoundFromText = inorganicsNotFoundFromText;
    }

    Integer getGoogleFoundFromText() {
        return googleFoundFromText;
    }

    void setGoogleFoundFromText(Integer googleFoundFromText) {
        this.googleFoundFromText = googleFoundFromText;
    }

    Integer getGoogleNotFoundFromText() {
        return googleNotFoundFromText;
    }

    void setGoogleNotFoundFromText(Integer googleNotFoundFromText) {
        this.googleNotFoundFromText = googleNotFoundFromText;
    }

    Integer getBingFoundFromText() {
        return bingFoundFromText;
    }

    void setBingFoundFromText(Integer bingFoundFromText) {
        this.bingFoundFromText = bingFoundFromText;
    }

    Integer getBingNotFoundFromText() {
        return bingNotFoundFromText;
    }

    void setBingNotFoundFromText(Integer bingNotFoundFromText) {
        this.bingNotFoundFromText = bingNotFoundFromText;
    }

    Integer getPaidBingQueries() {
        return paidBingQueries;
    }

    void setPaidBingQueries(Integer paidBingQueries) {
        this.paidBingQueries = paidBingQueries;
    }

    Integer getInorganicsLearned() {
        return inorganicsLearned;
    }

    void setInorganicsLearned(Integer inorganicsLearned) {
        this.inorganicsLearned = inorganicsLearned;
    }

    Integer getInorganicsAutocompleted() {
        return inorganicsAutocompleted;
    }

    void setInorganicsAutocompleted(Integer inorganicsAutocompleted) {
        this.inorganicsAutocompleted = inorganicsAutocompleted;
    }

    Integer getOrganicsFoundFromNameFromText() {
        return organicsFoundFromNameFromText;
    }

    void setOrganicsFoundFromNameFromText(Integer organicsFoundFromNameFromText) {
        this.organicsFoundFromNameFromText = organicsFoundFromNameFromText;
    }

    Integer getOrganicsNotFoundFromNameFromText() {
        return organicsNotFoundFromNameFromText;
    }

    void setOrganicsNotFoundFromNameFromText(Integer organicsNotFoundFromNameFromText) {
        this.organicsNotFoundFromNameFromText = organicsNotFoundFromNameFromText;
    }

    Integer getOrganicsSucceededFromStructure() {
        return organicsSucceededFromStructure;
    }

    void setOrganicsSucceededFromStructure(Integer organicsSucceededFromStructure) {
        this.organicsSucceededFromStructure = organicsSucceededFromStructure;
    }

    Integer getOrganicsFailedFromStructure() {
        return organicsFailedFromStructure;
    }

    void setOrganicsFailedFromStructure(Integer organicsFailedFromStructure) {
        this.organicsFailedFromStructure = organicsFailedFromStructure;
    }

    Integer getMolecularMassesSucceeded() {
        return molecularMassesSucceeded;
    }

    void setMolecularMassesSucceeded(Integer molecularMassesSucceeded) {
        this.molecularMassesSucceeded = molecularMassesSucceeded;
    }

    Integer getMolecularMassesFailed() {
        return molecularMassesFailed;
    }

    void setMolecularMassesFailed(Integer molecularMassesFailed) {
        this.molecularMassesFailed = molecularMassesFailed;
    }

}
