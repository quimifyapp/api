package com.quimify.api.settings;

import javax.persistence.*;

// This class represents each API version settings.

@Entity
@Table(name = "settings")
class SettingsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer version;

    // External API settings:

    @Column(nullable = false)
    private Boolean useBing;
    @Column(nullable = false)
    private Integer bingDailyLimit;

    @Column(nullable = false)
    private Boolean useGoogle;
    @Column(nullable = false)
    private Integer googleDailyLimit;

    // URLs:

    @Column(nullable = false)
    private String bingUrl;

    @Column(nullable = false)
    private String googleUrl;

    @Column(nullable = false)
    private String classifierAiUrl;

    @Column(nullable = false)
    private String userAgent; // For web scrapping

    // Getters and setters:

    Integer getVersion() {
        return version;
    }

    void setVersion(Integer version) {
        this.version = version;
    }

    Boolean getUseBing() {
        return useBing;
    }

    void setUseBing(Boolean useBing) {
        this.useBing = useBing;
    }

    Integer getBingDailyLimit() {
        return bingDailyLimit;
    }

    void setBingDailyLimit(Integer bingDailyLimit) {
        this.bingDailyLimit = bingDailyLimit;
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

    String getBingUrl() {
        return bingUrl;
    }

    void setBingUrl(String bingUrl) {
        this.bingUrl = bingUrl;
    }

    String getGoogleUrl() {
        return googleUrl;
    }

    void setGoogleUrl(String googleUrl) {
        this.googleUrl = googleUrl;
    }

    String getClassifierAiUrl() {
        return classifierAiUrl;
    }

    void setClassifierAiUrl(String classifierAiUrl) {
        this.classifierAiUrl = classifierAiUrl;
    }

    String getUserAgent() {
        return userAgent;
    }

    void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

}
