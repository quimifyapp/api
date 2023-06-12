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

    @Value("${quimify.api.google.key}")
    private String googleKey;

    @Value("${quimify.api.bing.free.key}")
    private String freeBingKey;

    @Value("${quimify.api.bing.paid.key}")
    private String paidBingKey;

    // Secret:

    public String getGoogleUrl() {
        return String.format(getSettings().getGoogleUrl(), googleKey);
    }

    public String getFreeBingKey() {
        return freeBingKey;
    }

    public String getPaidBingKey() {
        return paidBingKey;
    }

    // Private:

    private SettingsModel getSettings() {
        return settingsRepository.findByVersion(apiVersion);
    }

    // Trivial:

    public Boolean getUseGoogle() {
        return getSettings().getUseGoogle();
    }

    public Integer getGoogleDailyLimit() {
        return getSettings().getGoogleDailyLimit();
    }

    public Boolean getUseFreeBing() {
        return getSettings().getUseFreeBing();
    }

    public Integer getPaidBingDailyLimit() {
        return getSettings().getPaidBingDailyLimit();
    }

    public Boolean getUsePaidBing() {
        return getSettings().getUsePaidBing();
    }

    public String getBingUrl() {
        return getSettings().getBingUrl();
    }

    public String getClassifierUrl() {
        return getSettings().getClassifierUrl();
    }

    public String getUserAgent() {
        return getSettings().getUserAgent();
    }

}
