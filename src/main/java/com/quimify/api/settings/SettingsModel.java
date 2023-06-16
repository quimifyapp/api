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
    private String classifierUrl;

    @Column(nullable = false)
    private String userAgent; // For web scrapping

    // Getters and setters:

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getUseFreeBing() {
        return useFreeBing;
    }

    public void setUseFreeBing(Boolean useFreeBing) {
        this.useFreeBing = useFreeBing;
    }

    public Integer getFreeBingDailyLimit() {
        return freeBingDailyLimit;
    }

    public void setFreeBingDailyLimit(Integer freeBingDailyLimit) {
        this.freeBingDailyLimit = freeBingDailyLimit;
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

    public String getFreeBingUrl() {
        return freeBingUrl;
    }

    public void setFreeBingUrl(String freeBingUrl) {
        this.freeBingUrl = freeBingUrl;
    }

    public String getGoogleUrl() {
        return googleUrl;
    }

    public void setGoogleUrl(String googleUrl) {
        this.googleUrl = googleUrl;
    }

    public String getClassifierUrl() {
        return classifierUrl;
    }

    public void setClassifierUrl(String classifierUrl) {
        this.classifierUrl = classifierUrl;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

}
