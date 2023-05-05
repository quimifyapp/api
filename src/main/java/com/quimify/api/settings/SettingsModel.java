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

    Integer getVersion() {
        return version;
    }

    void setVersion(Integer version) {
        this.version = version;
    }

    Boolean getUseGoogle() {
        return useGoogle;
    }

    void setUseGoogle(Boolean useGoogle) {
        this.useGoogle = useGoogle;
    }

    Integer getGoogleDailyLimit() {
        return googleDailyLimit;
    }

    void setGoogleDailyLimit(Integer googleDailyLimit) {
        this.googleDailyLimit = googleDailyLimit;
    }

    String getGoogleUrl() {
        return googleUrl;
    }

    void setGoogleUrl(String googleUrl) {
        this.googleUrl = googleUrl;
    }

    Boolean getUseFreeBing() {
        return useFreeBing;
    }

    void setUseFreeBing(Boolean useFreeBing) {
        this.useFreeBing = useFreeBing;
    }

    String getFreeBingKey() {
        return freeBingKey;
    }

    void setFreeBingKey(String freeBingKey) {
        this.freeBingKey = freeBingKey;
    }

    Boolean getUsePaidBing() {
        return usePaidBing;
    }

    void setUsePaidBing(Boolean usePaidBing) {
        this.usePaidBing = usePaidBing;
    }

    Integer getPaidBingDailyLimit() {
        return paidBingDailyLimit;
    }

    void setPaidBingDailyLimit(Integer paidBingDailyLimit) {
        this.paidBingDailyLimit = paidBingDailyLimit;
    }

    String getPaidBingKey() {
        return paidBingKey;
    }

    void setPaidBingKey(String paidBingKey) {
        this.paidBingKey = paidBingKey;
    }

    String getBingUrl() {
        return bingUrl;
    }

    void setBingUrl(String bingUrl) {
        this.bingUrl = bingUrl;
    }

    String getUserAgent() {
        return userAgent;
    }

    void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

}
