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

    protected MetricsModel() {} // JPA needs it

    protected MetricsModel(Date date) {
        setDate(date);
    }

    // Incrementers:

    protected void incrementErrorsOccurred() {
        errorsOccurred += 1;
    }

    protected void incrementClientErrorsSent() {
        clientErrorsSent += 1;
    }

    protected void incrementReportsSent() {
        reportsSent += 1;
    }

    protected void incrementAndroidAccesses() {
        androidAccesses += 1;
    }

    protected void incrementIOSAccesses() {
        iosAccesses += 1;
    }

    protected void incrementWebAccesses() {
        webAccesses += 1;
    }

    protected void incrementInorganicsFoundFromText() {
        inorganicsFoundFromText += 1;
    }

    protected void incrementInorganicsNotFoundFromText() {
        inorganicsNotFoundFromText += 1;
    }

    protected void incrementGoogleFoundFromText() {
        googleFoundFromText += 1;
    }

    protected void incrementGoogleNotFoundFromText() {
        googleNotFoundFromText += 1;
    }

    protected void incrementBingFoundFromText() {
        bingFoundFromText += 1;
    }

    protected void incrementBingNotFoundFromText() {
        bingNotFoundFromText += 1;
    }

    protected void incrementPaidBingQueries() {
        paidBingQueries += 1;
    }

    protected void incrementInorganicsLearned() {
        inorganicsLearned += 1;
    }

    protected void incrementInorganicsAutocompleted() {
        inorganicsAutocompleted += 1;
    }

    protected void incrementOrganicsFoundFromNameFromText() {
        organicsFoundFromNameFromText += 1;
    }

    protected void incrementOrganicsNotFoundFromNameFromText() {
        organicsNotFoundFromNameFromText += 1;
    }

    protected void incrementOrganicsSucceededFromStructure() {
        organicsSucceededFromStructure += 1;
    }

    protected void incrementOrganicsFailedFromStructure() {
        organicsFailedFromStructure += 1;
    }

    protected void incrementMolecularMassesSucceeded() {
        molecularMassesSucceeded += 1;
    }

    protected void incrementMolecularMassesFailed() {
        molecularMassesFailed += 1;
    }

    // Getters and setters:

    protected Date getDate() {
        return date;
    }

    protected void setDate(Date date) {
        this.date = date;
    }

    protected Integer getErrorsOccurred() {
        return errorsOccurred;
    }

    protected void setErrorsOccurred(Integer errorsOccurred) {
        this.errorsOccurred = errorsOccurred;
    }

    protected Integer getClientErrorsSent() {
        return clientErrorsSent;
    }

    protected void setClientErrorsSent(Integer clientErrorsOccurred) {
        this.clientErrorsSent = clientErrorsOccurred;
    }

    protected Integer getReportsSent() {
        return reportsSent;
    }

    protected void setReportsSent(Integer reportsSent) {
        this.reportsSent = reportsSent;
    }

    protected Integer getAndroidAccesses() {
        return androidAccesses;
    }

    protected void setAndroidAccesses(Integer androidAccesses) {
        this.androidAccesses = androidAccesses;
    }

    protected Integer getIosAccesses() {
        return iosAccesses;
    }

    protected void setIosAccesses(Integer iosAccesses) {
        this.iosAccesses = iosAccesses;
    }

    protected Integer getWebAccesses() {
        return webAccesses;
    }

    protected void setWebAccesses(Integer webAccesses) {
        this.webAccesses = webAccesses;
    }

    protected Integer getInorganicsFoundFromText() {
        return inorganicsFoundFromText;
    }

    protected void setInorganicsFoundFromText(Integer inorganicsFoundFromText) {
        this.inorganicsFoundFromText = inorganicsFoundFromText;
    }

    protected Integer getInorganicsNotFoundFromText() {
        return inorganicsNotFoundFromText;
    }

    protected void setInorganicsNotFoundFromText(Integer inorganicsNotFoundFromText) {
        this.inorganicsNotFoundFromText = inorganicsNotFoundFromText;
    }

    protected Integer getGoogleFoundFromText() {
        return googleFoundFromText;
    }

    protected void setGoogleFoundFromText(Integer googleFoundFromText) {
        this.googleFoundFromText = googleFoundFromText;
    }

    protected Integer getGoogleNotFoundFromText() {
        return googleNotFoundFromText;
    }

    protected void setGoogleNotFoundFromText(Integer googleNotFoundFromText) {
        this.googleNotFoundFromText = googleNotFoundFromText;
    }

    protected Integer getBingFoundFromText() {
        return bingFoundFromText;
    }

    protected void setBingFoundFromText(Integer bingFoundFromText) {
        this.bingFoundFromText = bingFoundFromText;
    }

    protected Integer getBingNotFoundFromText() {
        return bingNotFoundFromText;
    }

    protected void setBingNotFoundFromText(Integer bingNotFoundFromText) {
        this.bingNotFoundFromText = bingNotFoundFromText;
    }

    protected Integer getPaidBingQueries() {
        return paidBingQueries;
    }

    protected void setPaidBingQueries(Integer paidBingQueries) {
        this.paidBingQueries = paidBingQueries;
    }

    protected Integer getInorganicsLearned() {
        return inorganicsLearned;
    }

    protected void setInorganicsLearned(Integer inorganicsLearned) {
        this.inorganicsLearned = inorganicsLearned;
    }

    protected Integer getInorganicsAutocompleted() {
        return inorganicsAutocompleted;
    }

    protected void setInorganicsAutocompleted(Integer inorganicsAutocompleted) {
        this.inorganicsAutocompleted = inorganicsAutocompleted;
    }

    protected Integer getOrganicsFoundFromNameFromText() {
        return organicsFoundFromNameFromText;
    }

    protected void setOrganicsFoundFromNameFromText(Integer organicsFoundFromNameFromText) {
        this.organicsFoundFromNameFromText = organicsFoundFromNameFromText;
    }

    protected Integer getOrganicsNotFoundFromNameFromText() {
        return organicsNotFoundFromNameFromText;
    }

    protected void setOrganicsNotFoundFromNameFromText(Integer organicsNotFoundFromNameFromText) {
        this.organicsNotFoundFromNameFromText = organicsNotFoundFromNameFromText;
    }

    protected Integer getOrganicsSucceededFromStructure() {
        return organicsSucceededFromStructure;
    }

    protected void setOrganicsSucceededFromStructure(Integer organicsSucceededFromStructure) {
        this.organicsSucceededFromStructure = organicsSucceededFromStructure;
    }

    protected Integer getOrganicsFailedFromStructure() {
        return organicsFailedFromStructure;
    }

    protected void setOrganicsFailedFromStructure(Integer organicsFailedFromStructure) {
        this.organicsFailedFromStructure = organicsFailedFromStructure;
    }

    protected Integer getMolecularMassesSucceeded() {
        return molecularMassesSucceeded;
    }

    protected void setMolecularMassesSucceeded(Integer molecularMassesSucceeded) {
        this.molecularMassesSucceeded = molecularMassesSucceeded;
    }

    protected Integer getMolecularMassesFailed() {
        return molecularMassesFailed;
    }

    protected void setMolecularMassesFailed(Integer molecularMassesFailed) {
        this.molecularMassesFailed = molecularMassesFailed;
    }

}
