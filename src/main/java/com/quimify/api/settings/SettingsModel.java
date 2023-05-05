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
    private Boolean useGoogle;
    @Column(nullable = false)
    private Integer googleDailyLimit;

    @Column(nullable = false)
    private Boolean useFreeBing;

    @Column(nullable = false)
    private Boolean usePaidBing;
    @Column(nullable = false)
    private Integer paidBingDailyLimit;

    // URLs:

    @Column(nullable = false)
    private String googleUrl;

    @Column(nullable = false)
    private String bingUrl;

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

    Boolean getUseFreeBing() {
        return useFreeBing;
    }

    void setUseFreeBing(Boolean useFreeBing) {
        this.useFreeBing = useFreeBing;
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

    String getGoogleUrl() {
        return googleUrl;
    }

    void setGoogleUrl(String googleUrl) {
        this.googleUrl = googleUrl;
    }

    String getBingUrl() {
        return bingUrl;
    }

    void setBingUrl(String bingUrl) {
        this.bingUrl = bingUrl;
    }

    String getClassifierUrl() {
        return classifierUrl;
    }

    void setClassifierUrl(String classifierUrl) {
        this.classifierUrl = classifierUrl;
    }

    String getUserAgent() {
        return userAgent;
    }

    void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

}
