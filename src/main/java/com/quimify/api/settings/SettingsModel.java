package com.quimify.api.settings;

import javax.persistence.*;

// This class represents each API version settings.

@Entity
@Table(name = "settings")
class SettingsModel {

    @Id
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

    Boolean getUseBing() {
        return useBing;
    }

    Integer getBingDailyLimit() {
        return bingDailyLimit;
    }

    Boolean getUseGoogle() {
        return useGoogle;
    }

    Integer getGoogleDailyLimit() {
        return googleDailyLimit;
    }

    String getBingUrl() {
        return bingUrl;
    }

    String getGoogleUrl() {
        return googleUrl;
    }

    String getClassifierAiUrl() {
        return classifierAiUrl;
    }

    String getUserAgent() {
        return userAgent;
    }

}
