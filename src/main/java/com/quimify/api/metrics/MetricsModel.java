package com.quimify.api.metrics;

// Esta clase representa un día de métricas.

import javax.persistence.*;
import java.util.Date;

@Entity // Es un modelo real
@Table(name = "metrics") // En la tabla 'metrics' de la DB
class MetricsModel {

    @Id
    @Temporal(TemporalType.DATE)
    private Date date;

    // Su suma equivale al total de accesos al cliente:

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer androidAccesses = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer iosAccesses = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer webAccesses = 0;

    // Su suma equivale al total de peticiones de inorgánicos:

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicsFoundFromText = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicsNotFoundFromText = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicsFoundFromPicture = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicsNotFoundFromPicture = 0;

    // Su suma equivale al total de búsquedas web de inorgánicos:

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer googleFoundFromText = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer googleNotFoundFromText = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer googleFoundFromPicture = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer googleNotFoundFromPicture = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer bingFoundFromText = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer bingNotFoundFromText = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer bingFoundFromPicture = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer bingNotFoundFromPicture = 0;

    // Aparte, las búsquedas en Bing *de pago* para poder limitarlas:

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer paidBingQueries = 0;

    // Inorgánicos añadidos a la base de datos:

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicsLearned = 0;

    // Compleciones correctas de inorgánicos:

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganicsAutocompleted = 0;

    // Su suma equivale al total de peticiones de formular orgánicos:

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer organicsFoundFromNameFromText = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer organicsNotFoundFromNameFromText = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer organicsFoundFromNameFromPicture = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer organicsNotFoundFromNameFromPicture = 0;

    // Su suma equivale al total de peticiones de nombrar orgánicos:

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer organicsSucceededFromStructure = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer organicsFailedFromStructure = 0; // TODO

    // Su suma equivale al total de peticiones de calcular masas moleculares:

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer molecularMassesSucceeded = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer molecularMassesFailed = 0;

    // Reportes de errores:

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer reportsSent = 0;

    // --------------------------------------------------------------------------------

    // Constructores:

    protected MetricsModel() {} // Lo pide JPA

    protected MetricsModel(Date date) {
        setDate(date);
    }

    // Incrementadores:

    protected void nuevoAccesoAndroid() {
        androidAccesses += 1;
    }

    protected void nuevoAccesoIOS() {
        iosAccesses += 1;
    }

    protected void nuevoAccesoWeb() {
        webAccesses += 1;
    }

    protected void nuevoInorganicoTecladoEncontrado() {
        inorganicsFoundFromText += 1;
    }

    protected void nuevoInorganicoTecladoNoEncontrado() {
        inorganicsNotFoundFromText += 1;
    }

    protected void nuevoInorganicoFotoEncontrado() {
        inorganicsFoundFromPicture += 1;
    }

    protected void nuevoInorganicoFotoNoEncontrado() {
        inorganicsNotFoundFromPicture += 1;
    }

    protected void nuevoGoogleTecladoEncontrado() {
        googleFoundFromText += 1;
    }

    protected void nuevoGoogleTecladoNoEncontrado() {
        googleNotFoundFromText += 1;
    }

    protected void nuevoGoogleFotoEncontrado() {
        googleFoundFromPicture += 1;
    }

    protected void nuevoGoogleFotoNoEncontrado() {
        googleNotFoundFromPicture += 1;
    }

    protected void nuevoBingTecladoEncontrado() {
        bingFoundFromText += 1;
    }

    protected void nuevoBingTecladoNoEncontrado() {
        bingNotFoundFromText += 1;
    }

    protected void nuevoBingFotoEncontrado() {
        bingFoundFromPicture += 1;
    }

    protected void nuevoBingFotoNoEncontrado() {
        bingNotFoundFromPicture += 1;
    }

    protected void nuevoBingPagoBuscado() {
        paidBingQueries += 1;
    }

    protected void nuevoInorganicoNuevo() {
        inorganicsLearned += 1;
    }

    protected void nuevoInorganicoAutocompletado() {
        inorganicsAutocompleted += 1;
    }

    protected void nuevoFormularOrganicoTecladoEncontrado() {
        organicsFoundFromNameFromText += 1;
    }

    protected void nuevoFormularOrganicoTecladoNoEncontrado() {
        organicsNotFoundFromNameFromText += 1;
    }

    protected void nuevoFormularOrganicoFotoEncontrado() {
        organicsFoundFromNameFromPicture += 1;
    }

    protected void nuevoFormularOrganicoFotoNoEncontrado() {
        organicsNotFoundFromNameFromPicture += 1;
    }

    protected void nuevoNombrarOrganicoAbiertoBuscado() {
        organicsSucceededFromStructure += 1;
    }

    protected void countOrganicsFailedFromStructure() {
        organicsFailedFromStructure += 1;
    }

    protected void nuevoMasaMolecularEncontrado() {
        molecularMassesSucceeded += 1;
    }

    protected void nuevoMasaMolecularNoEncontrado() {
        molecularMassesFailed += 1;
    }

    protected void nuevoReporte() {
        reportsSent += 1;
    }

    // Getters y setters:

    protected Date getDate() {
        return date;
    }

    protected void setDate(Date date) {
        this.date = date;
    }

    protected Integer getAndroidAccesses() {
        return androidAccesses;
    }

    protected void setAndroidAccesses(Integer androidAccesses) {
        this.androidAccesses = androidAccesses;
    }

    protected Integer getiIosAccesses() {
        return iosAccesses;
    }

    protected void setiIosAccesses(Integer iOSAccesses) {
        this.iosAccesses = iOSAccesses;
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

    protected Integer getInorganicsFoundFromPicture() {
        return inorganicsFoundFromPicture;
    }

    protected void setInorganicsFoundFromPicture(Integer inorganicsFoundFromPicture) {
        this.inorganicsFoundFromPicture = inorganicsFoundFromPicture;
    }

    protected Integer getInorganicsNotFoundFromPicture() {
        return inorganicsNotFoundFromPicture;
    }

    protected void setInorganicsNotFoundFromPicture(Integer inorganicsNotFoundFromPicture) {
        this.inorganicsNotFoundFromPicture = inorganicsNotFoundFromPicture;
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

    protected Integer getGoogleFoundFromPicture() {
        return googleFoundFromPicture;
    }

    protected void setGoogleFoundFromPicture(Integer googleFoundFromPicture) {
        this.googleFoundFromPicture = googleFoundFromPicture;
    }

    protected Integer getGoogleNotFoundFromPicture() {
        return googleNotFoundFromPicture;
    }

    protected void setGoogleNotFoundFromPicture(Integer googleNotFoundFromPicture) {
        this.googleNotFoundFromPicture = googleNotFoundFromPicture;
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

    protected Integer getBingFoundFromPicture() {
        return bingFoundFromPicture;
    }

    protected void setBingFoundFromPicture(Integer bingFoundFromPicture) {
        this.bingFoundFromPicture = bingFoundFromPicture;
    }

    protected Integer getBingNotFoundFromPicture() {
        return bingNotFoundFromPicture;
    }

    protected void setBingNotFoundFromPicture(Integer bingNotFoundFromPicture) {
        this.bingNotFoundFromPicture = bingNotFoundFromPicture;
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

    protected Integer getOrganicsFoundFromNameFromPicture() {
        return organicsFoundFromNameFromPicture;
    }

    protected void setOrganicsFoundFromNameFromPicture(Integer organicsFoundFromNameFromPicture) {
        this.organicsFoundFromNameFromPicture = organicsFoundFromNameFromPicture;
    }

    protected Integer getOrganicsNotFoundFromNameFromPicture() {
        return organicsNotFoundFromNameFromPicture;
    }

    protected void setOrganicsNotFoundFromNameFromPicture(Integer organicsNotFoundFromNameFromPicture) {
        this.organicsNotFoundFromNameFromPicture = organicsNotFoundFromNameFromPicture;
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

    protected Integer getReportsSent() {
        return reportsSent;
    }

    protected void setReportsSent(Integer reportsSent) {
        this.reportsSent = reportsSent;
    }

}
