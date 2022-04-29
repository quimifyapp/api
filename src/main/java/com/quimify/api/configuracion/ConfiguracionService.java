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

    public Boolean getApiGoogleON() {
        return configuracionRepository.encontrarApiGoogleON(VERSION);
    }

    public String getApiGoogleURL() {
        return configuracionRepository.encontrarApiGoogleURL(VERSION);
    }

}
