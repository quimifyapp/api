package com.quimify.api.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Esta clase procesa la configuración.

@Service
public
class SettingsService {

    @Autowired
    SettingsRepository settingsRepository; // Conexión con la DB

    private static final Integer apiVersion = 3;

    // --------------------------------------------------------------------------------

    public Boolean getGoogleON() {
        return settingsRepository.findByVersion(apiVersion).getUseGoogle();
    }

    public Integer getGoogleLimit() {
        return settingsRepository.findByVersion(apiVersion).getGoogleDailyLimit();
    }

    public String getGoogleURL() {
        return settingsRepository.findByVersion(apiVersion).getGoogleUrl();
    }

    public String getBingURL() {
        return settingsRepository.findByVersion(apiVersion).getBingUrl();
    }

    public Boolean getFreeBingON() {
        return settingsRepository.findByVersion(apiVersion).getUseFreeBing();
    }

    public Integer getPaidBingLimit() {
        return settingsRepository.findByVersion(apiVersion).getPaidBingDailyLimit();
    }

    public String getFreeBingKey() {
        return settingsRepository.findByVersion(apiVersion).getFreeBingKey();
    }

    public Boolean getPaidBingON() {
        return settingsRepository.findByVersion(apiVersion).getUsePaidBing();
    }

    public String getPaidBingKey() {
        return settingsRepository.findByVersion(apiVersion).getPaidBingKey();
    }

    public String getUserAgent() {
        return settingsRepository.findByVersion(apiVersion).getUserAgent();
    }

}
