package com.quimify.api.settings;

import javax.persistence.*;

// This class represents each API version settings.

@Entity
@Table(name = "settings")
class SettingsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer version; // API version

    // Google API:

    @Column(nullable = false)
    private Boolean useGoogle = false; // Switch
    @Column(nullable = false)
    private Integer googleDailyLimit;
    @Column(nullable = false)
    private String googleUrl; // TODO env

    // Free Bing API:

    @Column(nullable = false)
    private Boolean useFreeBing = false; // Switch
    @Column(nullable = false)
    private String freeBingKey; // TODO env

    // Paid Bing API:

    @Column(nullable = false)
    private Boolean usePaidBing = false; // Switch
    @Column(nullable = false)
    private Integer paidBingDailyLimit;
    @Column(nullable = false)
    private String paidBingKey; // TODO env

    // Both Bing APIs:

    @Column(nullable = false)
    private String bingUrl;

    // Classifier API:

    @Column(nullable = false)
    private String classifierUrl;

    @Column(nullable = false)
    private String userAgent; // For web scrapping

    // Getters & setters:

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
