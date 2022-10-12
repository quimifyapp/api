package com.quimify.api.configuracion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Esta clase procesa la configuración.

@Service
public class ConfiguracionService {

    @Autowired
    ConfiguracionRepository configuracionRepository; // Conexión con la DB

    private static final Integer apiVersion = 1;

    // --------------------------------------------------------------------------------

    public Boolean getGoogleON() {
        return configuracionRepository.findByVersion(apiVersion).getGoogle_on();
    }

    public Integer getGoogleLimite() {
        return configuracionRepository.findByVersion(apiVersion).getGoogle_limite();
    }

    public String getGoogleURL() {
        return configuracionRepository.findByVersion(apiVersion).getGoogle_url();
    }

    public String getBingURL() {
        return configuracionRepository.findByVersion(apiVersion).getBing_url();
    }

    public Boolean getBingGratisON() {
        return configuracionRepository.findByVersion(apiVersion).getBing_gratis_on();
    }

    public Integer getBingPagoLimite() {
        return configuracionRepository.findByVersion(apiVersion).getBing_pago_limite();
    }

    public String getBingGratisKey() {
        return configuracionRepository.findByVersion(apiVersion).getBing_gratis_key();
    }

    public Boolean getBingPagoON() {
        return configuracionRepository.findByVersion(apiVersion).getBing_pago_on();
    }

    public String getBingPagoKey() {
        return configuracionRepository.findByVersion(apiVersion).getBing_pago_key();
    }

    public String getUserAgent() {
        return configuracionRepository.findByVersion(apiVersion).getUser_agent();
    }

}
