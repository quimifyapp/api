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
    private Integer organicsFailedFromStructure = 0;

    // Su suma equivale al total de peticiones de calcular masas moleculares:

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer molecularMassesSucceeded = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer molecularMassesFailed = 0;

    // API errors:

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer errorsOccurred = 0;

    // Client reports:

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

    protected void countErrorOccurred() {
        errorsOccurred += 1;
    }

    protected void countReportSent() {
        reportsSent += 1;
    }

    // Getters y setters:

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public Integer getWebAccesses() {
        return webAccesses;
    }

    public void setWebAccesses(Integer webAccesses) {
        this.webAccesses = webAccesses;
    }

    public Integer getInorganicsFoundFromText() {
        return inorganicsFoundFromText;
    }

    public void setInorganicsFoundFromText(Integer inorganicsFoundFromText) {
        this.inorganicsFoundFromText = inorganicsFoundFromText;
    }

    public Integer getInorganicsNotFoundFromText() {
        return inorganicsNotFoundFromText;
    }

    public void setInorganicsNotFoundFromText(Integer inorganicsNotFoundFromText) {
        this.inorganicsNotFoundFromText = inorganicsNotFoundFromText;
    }

    public Integer getInorganicsFoundFromPicture() {
        return inorganicsFoundFromPicture;
    }

    public void setInorganicsFoundFromPicture(Integer inorganicsFoundFromPicture) {
        this.inorganicsFoundFromPicture = inorganicsFoundFromPicture;
    }

    public Integer getInorganicsNotFoundFromPicture() {
        return inorganicsNotFoundFromPicture;
    }

    public void setInorganicsNotFoundFromPicture(Integer inorganicsNotFoundFromPicture) {
        this.inorganicsNotFoundFromPicture = inorganicsNotFoundFromPicture;
    }

    public Integer getGoogleFoundFromText() {
        return googleFoundFromText;
    }

    public void setGoogleFoundFromText(Integer googleFoundFromText) {
        this.googleFoundFromText = googleFoundFromText;
    }

    public Integer getGoogleNotFoundFromText() {
        return googleNotFoundFromText;
    }

    public void setGoogleNotFoundFromText(Integer googleNotFoundFromText) {
        this.googleNotFoundFromText = googleNotFoundFromText;
    }

    public Integer getGoogleFoundFromPicture() {
        return googleFoundFromPicture;
    }

    public void setGoogleFoundFromPicture(Integer googleFoundFromPicture) {
        this.googleFoundFromPicture = googleFoundFromPicture;
    }

    public Integer getGoogleNotFoundFromPicture() {
        return googleNotFoundFromPicture;
    }

    public void setGoogleNotFoundFromPicture(Integer googleNotFoundFromPicture) {
        this.googleNotFoundFromPicture = googleNotFoundFromPicture;
    }

    public Integer getBingFoundFromText() {
        return bingFoundFromText;
    }

    public void setBingFoundFromText(Integer bingFoundFromText) {
        this.bingFoundFromText = bingFoundFromText;
    }

    public Integer getBingNotFoundFromText() {
        return bingNotFoundFromText;
    }

    public void setBingNotFoundFromText(Integer bingNotFoundFromText) {
        this.bingNotFoundFromText = bingNotFoundFromText;
    }

    public Integer getBingFoundFromPicture() {
        return bingFoundFromPicture;
    }

    public void setBingFoundFromPicture(Integer bingFoundFromPicture) {
        this.bingFoundFromPicture = bingFoundFromPicture;
    }

    public Integer getBingNotFoundFromPicture() {
        return bingNotFoundFromPicture;
    }

    public void setBingNotFoundFromPicture(Integer bingNotFoundFromPicture) {
        this.bingNotFoundFromPicture = bingNotFoundFromPicture;
    }

    public Integer getPaidBingQueries() {
        return paidBingQueries;
    }

    public void setPaidBingQueries(Integer paidBingQueries) {
        this.paidBingQueries = paidBingQueries;
    }

    public Integer getInorganicsLearned() {
        return inorganicsLearned;
    }

    public void setInorganicsLearned(Integer inorganicsLearned) {
        this.inorganicsLearned = inorganicsLearned;
    }

    public Integer getInorganicsAutocompleted() {
        return inorganicsAutocompleted;
    }

    public void setInorganicsAutocompleted(Integer inorganicsAutocompleted) {
        this.inorganicsAutocompleted = inorganicsAutocompleted;
    }

    public Integer getOrganicsFoundFromNameFromText() {
        return organicsFoundFromNameFromText;
    }

    public void setOrganicsFoundFromNameFromText(Integer organicsFoundFromNameFromText) {
        this.organicsFoundFromNameFromText = organicsFoundFromNameFromText;
    }

    public Integer getOrganicsNotFoundFromNameFromText() {
        return organicsNotFoundFromNameFromText;
    }

    public void setOrganicsNotFoundFromNameFromText(Integer organicsNotFoundFromNameFromText) {
        this.organicsNotFoundFromNameFromText = organicsNotFoundFromNameFromText;
    }

    public Integer getOrganicsFoundFromNameFromPicture() {
        return organicsFoundFromNameFromPicture;
    }

    public void setOrganicsFoundFromNameFromPicture(Integer organicsFoundFromNameFromPicture) {
        this.organicsFoundFromNameFromPicture = organicsFoundFromNameFromPicture;
    }

    public Integer getOrganicsNotFoundFromNameFromPicture() {
        return organicsNotFoundFromNameFromPicture;
    }

    public void setOrganicsNotFoundFromNameFromPicture(Integer organicsNotFoundFromNameFromPicture) {
        this.organicsNotFoundFromNameFromPicture = organicsNotFoundFromNameFromPicture;
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

    public Integer getErrorsOccurred() {
        return errorsOccurred;
    }

    public void setErrorsOccurred(Integer errorsOccurred) {
        this.errorsOccurred = errorsOccurred;
    }

    public Integer getReportsSent() {
        return reportsSent;
    }

    public void setReportsSent(Integer reportsSent) {
        this.reportsSent = reportsSent;
    }

}
