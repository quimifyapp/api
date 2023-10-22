package com.quimify.api.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// This class implements settings logic.

@Service
public class SettingsService {

    @Autowired
    SettingsRepository settingsRepository; // DB connection

    private static final int apiVersion = 5;

    // Environmental variables:

    @Value("${quimify.api.bing.key}")
    private String bingKey;

    @Value("${quimify.api.google.key}")
    private String googleKey;

    // Secret:

    public String getBingKey() {
        return bingKey;
    }

    public String getGoogleKey() {
        return googleKey;
    }

    // Private:

    private SettingsModel getSettings() {
        return settingsRepository.findByVersion(apiVersion);
    }

    // Trivial:

    public Boolean getUseBing() {
        return getSettings().getUseBing();
    }

    public String getBingUrl() {
        return getSettings().getBingUrl();
    }

    public Integer getBingDailyLimit() {
        return getSettings().getBingDailyLimit();
    }

    public Boolean getUseGoogle() {
        return getSettings().getUseGoogle();
    }

    public String getGoogleUrl() {
        return getSettings().getGoogleUrl();
    }

    public Integer getGoogleDailyLimit() {
        return getSettings().getGoogleDailyLimit();
    }

    public String getClassifierAiUrl() {
        return getSettings().getClassifierAiUrl();
    }

    public String getUserAgent() {
        return getSettings().getUserAgent();
    }

}
