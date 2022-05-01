package com.quimify.api.configuracion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Esta clase procesa la configuración.

@Service
public class ConfiguracionService {

    @Autowired
    private ConfiguracionRepository configuracionRepository; // Conexión con la DB

    public static final Integer VERSION = 1;

    // --------------------------------------------------------------------------------

    public Boolean getGoogleON() {
        return configuracionRepository.encontrarGoogleON(VERSION);
    }

    public String getGoogleURL() {
        return configuracionRepository.encontrarGoogleURL(VERSION);
    }

    public String getBingURL() {
        return configuracionRepository.encontrarBingURL(VERSION);
    }

    public Boolean getBingGratisON() {
        return configuracionRepository.encontrarBingGratisON(VERSION);
    }

    public String getBingGratisKey() {
        return configuracionRepository.encontrarBingGratisKey(VERSION);
    }

    public Boolean getBingPagoON() {
        return configuracionRepository.encontrarBingPagoON(VERSION);
    }

    public String getBingPagoKey() {
        return configuracionRepository.encontrarBingPagoKey(VERSION);
    }

}
