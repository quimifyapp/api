package com.quimify.api.configuracion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Esta clase procesa la configuración.

@Service
public class ConfiguracionService {

    @Autowired
    ConfiguracionRepository configuracionRepository; // Conexión con la DB

    public static final Integer VERSION = 1;

    // --------------------------------------------------------------------------------

    public Boolean getGoogleON() {
        return configuracionRepository.findByVersion(VERSION).getGoogle_on();
    }

    public Integer getGoogleLimite() {
        return configuracionRepository.findByVersion(VERSION).getGoogle_limite();
    }

    public String getGoogleURL() {
        return configuracionRepository.findByVersion(VERSION).getGoogle_url();
    }

    public String getBingURL() {
        return configuracionRepository.findByVersion(VERSION).getBing_url();
    }

    public Boolean getBingGratisON() {
        return configuracionRepository.findByVersion(VERSION).getBing_gratis_on();
    }

    public Integer getBingPagoLimite() {
        return configuracionRepository.findByVersion(VERSION).getBing_pago_limite();
    }

    public String getBingGratisKey() {
        return configuracionRepository.findByVersion(VERSION).getBing_gratis_key();
    }

    public Boolean getBingPagoON() {
        return configuracionRepository.findByVersion(VERSION).getBing_pago_on();
    }

    public String getBingPagoKey() {
        return configuracionRepository.findByVersion(VERSION).getBing_pago_key();
    }

    public String getUserAgent() {
        return configuracionRepository.findByVersion(VERSION).getUser_agent();
    }

}
