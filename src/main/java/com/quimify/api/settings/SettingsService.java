package com.quimify.api.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Esta clase procesa la configuración.

@Service
public
class SettingsService {

    @Autowired
    SettingsRepository settingsRepository; // Conexión con la DB

    private static final Integer apiVersion = 2;

    // --------------------------------------------------------------------------------

    public Boolean getGoogleON() {
        return settingsRepository.findByVersion(apiVersion).getUseGoogle();
    }

    public Integer getGoogleLimite() {
        return settingsRepository.findByVersion(apiVersion).getGoogleDailyLimit();
    }

    public String getGoogleURL() {
        return settingsRepository.findByVersion(apiVersion).getGoogleUrl();
    }

    public String getBingURL() {
        return settingsRepository.findByVersion(apiVersion).getBingUrl();
    }

    public Boolean getBingGratisON() {
        return settingsRepository.findByVersion(apiVersion).getUseFreeBing();
    }

    public Integer getBingPagoLimite() {
        return settingsRepository.findByVersion(apiVersion).getPaidBingDailyLimit();
    }

    public String getBingGratisKey() {
        return settingsRepository.findByVersion(apiVersion).getFreeBingKey();
    }

    public Boolean getBingPagoON() {
        return settingsRepository.findByVersion(apiVersion).getUsePaidBing();
    }

    public String getBingPagoKey() {
        return settingsRepository.findByVersion(apiVersion).getPaidBingKey();
    }

    public String getUserAgent() {
        return settingsRepository.findByVersion(apiVersion).getUserAgent();
    }

}
