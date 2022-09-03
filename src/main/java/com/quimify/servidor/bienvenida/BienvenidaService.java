package com.quimify.servidor.bienvenida;

import com.quimify.servidor.configuracion.ConfiguracionService;
import com.quimify.servidor.metricas.MetricasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Esta clase procesa la bienvenida.

@Service
public class BienvenidaService {

	@Autowired
	ConfiguracionService configuracionService; // Procesos de la configuración

	@Autowired
	MetricasService metricaService; // Procesos de las métricas diarias

	public BienvenidaResultado bienvenida() {
		BienvenidaResultado resultado = new BienvenidaResultado(
				configuracionService.getActualizacionDisponible(),
				configuracionService.getActualizacionObligatoria(),
				configuracionService.getActualizacionDetalles(),
				configuracionService.getMensajePresente(),
				configuracionService.getMensajeTitulo(),
				configuracionService.getMensajeDetalles(),
				configuracionService.getMensajeEnlacePresente(),
				configuracionService.getMensajeEnlaceNombre(),
				configuracionService.getMensajeEnlace()
		);

		metricaService.contarAcceso();

		return resultado;
	}

}
