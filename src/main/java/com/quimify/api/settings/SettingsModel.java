package com.quimify.api.settings;

import javax.persistence.*;

// Esta clase representa la configuración de cada versión del servidor.

@Entity // Es un modelo real
@Table(name = "settings") // En la tabla 'settings' de la DB
public class SettingsModel {

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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getUseGoogle() {
        return useGoogle;
    }

    public void setUseGoogle(Boolean useGoogle) {
        this.useGoogle = useGoogle;
    }

    public Integer getGoogleDailyLimit() {
        return googleDailyLimit;
    }

    public void setGoogleDailyLimit(Integer googleDailyLimit) {
        this.googleDailyLimit = googleDailyLimit;
    }

    public String getGoogleUrl() {
        return googleUrl;
    }

    public void setGoogleUrl(String googleUrl) {
        this.googleUrl = googleUrl;
    }

    public Boolean getUseFreeBing() {
        return useFreeBing;
    }

    public void setUseFreeBing(Boolean useFreeBing) {
        this.useFreeBing = useFreeBing;
    }

    public String getFreeBingKey() {
        return freeBingKey;
    }

    public void setFreeBingKey(String freeBingKey) {
        this.freeBingKey = freeBingKey;
    }

    public Boolean getUsePaidBing() {
        return usePaidBing;
    }

    public void setUsePaidBing(Boolean usePaidBing) {
        this.usePaidBing = usePaidBing;
    }

    public Integer getPaidBingDailyLimit() {
        return paidBingDailyLimit;
    }

    public void setPaidBingDailyLimit(Integer paidBingDailyLimit) {
        this.paidBingDailyLimit = paidBingDailyLimit;
    }

    public String getPaidBingKey() {
        return paidBingKey;
    }

    public void setPaidBingKey(String paidBingKey) {
        this.paidBingKey = paidBingKey;
    }

    public String getBingUrl() {
        return bingUrl;
    }

    public void setBingUrl(String bingUrl) {
        this.bingUrl = bingUrl;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

}
