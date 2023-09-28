package com.quimify.api.settings;

import javax.persistence.*;

// This class represents each API version settings.

@Entity
@Table(name = "settings")
class SettingsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer version;

    // External API settings:

    @Column(nullable = false)
    private Boolean useFreeBing;
    @Column(nullable = false)
    private Integer freeBingDailyLimit;

    @Column(nullable = false)
    private Boolean useGoogle;
    @Column(nullable = false)
    private Integer googleDailyLimit;

    // URLs:

    @Column(nullable = false)
    private String freeBingUrl;

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

    Boolean getUseFreeBing() {
        return useFreeBing;
    }

    void setUseFreeBing(Boolean useFreeBing) {
        this.useFreeBing = useFreeBing;
    }

    Integer getFreeBingDailyLimit() {
        return freeBingDailyLimit;
    }

    void setFreeBingDailyLimit(Integer freeBingDailyLimit) {
        this.freeBingDailyLimit = freeBingDailyLimit;
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

    String getFreeBingUrl() {
        return freeBingUrl;
    }

    void setFreeBingUrl(String freeBingUrl) {
        this.freeBingUrl = freeBingUrl;
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
