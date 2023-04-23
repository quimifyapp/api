package com.quimify.api.settings;

import javax.persistence.*;

// Esta clase representa la configuración de cada versión del servidor. // TODO translate comments

@Entity // Es un modelo real
@Table(name = "settings") // En la tabla 'settings' de la DB
class SettingsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer version; // Se corresponde con la versión del servidor

    // API de Google:

    @Column(nullable = false)
    private Boolean useGoogle = false; // Interruptor
    @Column(nullable = false)
    private Integer googleDailyLimit; // Nº máx. de búsquedas diarias
    @Column(nullable = false)
    private String googleUrl; // URL + key

    // API de Bing gratis:

    @Column(nullable = false)
    private Boolean useFreeBing = false; // Interruptor
    @Column(nullable = false)
    private String freeBingKey; // Suscripción

    // API de Bing de pago:

    @Column(nullable = false)
    private Boolean usePaidBing = false; // Interruptor
    @Column(nullable = false)
    private Integer paidBingDailyLimit; // Nº máx. de búsquedas diarias
    @Column(nullable = false)
    private String paidBingKey; // Suscripción

    // API de Bing:

    @Column(nullable = false)
    private String bingUrl; // URL

    // Para FQ.com:

    @Column(nullable = false)
    private String userAgent; // Requisito HTTP para parecer un visitante corriente

    // --------------------------------------------------------------------------------

    // Getters y setters:

    protected Integer getVersion() {
        return version;
    }

    protected void setVersion(Integer version) {
        this.version = version;
    }

    protected Boolean getUseGoogle() {
        return useGoogle;
    }

    protected void setUseGoogle(Boolean useGoogle) {
        this.useGoogle = useGoogle;
    }

    protected Integer getGoogleDailyLimit() {
        return googleDailyLimit;
    }

    protected void setGoogleDailyLimit(Integer googleDailyLimit) {
        this.googleDailyLimit = googleDailyLimit;
    }

    protected String getGoogleUrl() {
        return googleUrl;
    }

    protected void setGoogleUrl(String googleUrl) {
        this.googleUrl = googleUrl;
    }

    protected Boolean getUseFreeBing() {
        return useFreeBing;
    }

    protected void setUseFreeBing(Boolean useFreeBing) {
        this.useFreeBing = useFreeBing;
    }

    protected String getFreeBingKey() {
        return freeBingKey;
    }

    protected void setFreeBingKey(String freeBingKey) {
        this.freeBingKey = freeBingKey;
    }

    protected Boolean getUsePaidBing() {
        return usePaidBing;
    }

    protected void setUsePaidBing(Boolean usePaidBing) {
        this.usePaidBing = usePaidBing;
    }

    protected Integer getPaidBingDailyLimit() {
        return paidBingDailyLimit;
    }

    protected void setPaidBingDailyLimit(Integer paidBingDailyLimit) {
        this.paidBingDailyLimit = paidBingDailyLimit;
    }

    protected String getPaidBingKey() {
        return paidBingKey;
    }

    protected void setPaidBingKey(String paidBingKey) {
        this.paidBingKey = paidBingKey;
    }

    protected String getBingUrl() {
        return bingUrl;
    }

    protected void setBingUrl(String bingUrl) {
        this.bingUrl = bingUrl;
    }

    protected String getUserAgent() {
        return userAgent;
    }

    protected void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

}
