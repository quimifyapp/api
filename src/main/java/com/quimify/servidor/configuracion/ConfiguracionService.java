package com.quimify.servidor.configuracion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Esta clase procesa la configuración.

@Service
public class ConfiguracionService {

    @Autowired
    ConfiguracionRepository configuracionRepository; // Conexión con la DB

    public static final Integer VERSION = 0;

    // --------------------------------------------------------------------------------

    public Boolean getActualizacionDisponible() {
        return configuracionRepository.findByVersion(VERSION).getActualizacion_disponible();
    }

    public Boolean getActualizacionObligatoria() {
        return configuracionRepository.findByVersion(VERSION).getActualizacion_obligatoria();
    }

    public String getActualizacionDetalles() {
        return configuracionRepository.findByVersion(VERSION).getActualizacion_detalles();
    }

    public Boolean getMensajePresente() {
        return configuracionRepository.findByVersion(VERSION).getMensaje_presente();
    }

    public String getMensajeTitulo() {
        return configuracionRepository.findByVersion(VERSION).getMensaje_titulo();
    }

    public String getMensajeDetalles() {
        return configuracionRepository.findByVersion(VERSION).getMensaje_detalles();
    }

    public Boolean getMensajeEnlacePresente() {
        return configuracionRepository.findByVersion(VERSION).getMensaje_enlace_presente();
    }

    public String getMensajeEnlaceNombre() {
        return configuracionRepository.findByVersion(VERSION).getMensaje_enlace_nombre();
    }

    public String getMensajeEnlace() {
        return configuracionRepository.findByVersion(VERSION).getMensaje_enlace();
    }

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
