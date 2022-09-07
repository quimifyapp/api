package com.quimify.api.bienvenida;

import com.quimify.api.configuracion.ConfiguracionService;
import com.quimify.api.metricas.MetricasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Esta clase procesa la bienvenida.

@Service
public class BienvenidaService {

	@Autowired
	ConfiguracionService configuracionService; // Procesos de la configuración

	@Autowired
	MetricasService metricasService; // Procesos de las métricas diarias

	public BienvenidaResultado bienvenida(Short plataforma) {
		BienvenidaResultado resultado = plataforma != MetricasService.WEB
				? new BienvenidaResultado(
				configuracionService.getActualizacionDisponible(),
				configuracionService.getActualizacionObligatoria(),
				configuracionService.getActualizacionDetalles(),
				configuracionService.getMensajePresente(),
				configuracionService.getMensajeTitulo(),
				configuracionService.getMensajeDetalles(),
				configuracionService.getMensajeEnlacePresente(),
				configuracionService.getMensajeEnlaceNombre(),
				configuracionService.getMensajeEnlace())
				: new BienvenidaResultado(
				false,
				null,
				null,
				configuracionService.getMensajePresente(),
				configuracionService.getMensajeTitulo(),
				configuracionService.getMensajeDetalles(),
				configuracionService.getMensajeEnlacePresente(),
				configuracionService.getMensajeEnlaceNombre(),
				configuracionService.getMensajeEnlace()
		);

		metricasService.contarAcceso(plataforma);

		return resultado;
	}

}
