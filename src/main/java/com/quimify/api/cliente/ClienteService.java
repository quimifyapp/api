package com.quimify.api.cliente;

import com.quimify.api.metricas.MetricasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Esta clase procesa la bienvenida.

@Service
public class ClienteService {

	@Autowired
	ClienteRepository clienteRepository; // Conexión con la DB

	@Autowired
	MetricasService metricasService; // Procesos de las métricas diarias

	public static final short ANDROID = 0;
	public static final short IOS = 1;
	public static final short WEB = 2;

	public ClienteResultado acceso(Integer version, Short plataforma) {
		ClienteModel cliente = clienteRepository.findByVersion(version);

		ClienteResultado resultado = plataforma != WEB
			? new ClienteResultado(
				cliente.getActualizacion_disponible(),
				cliente.getActualizacion_obligatoria(),
				cliente.getActualizacion_detalles(),
				cliente.getMensaje_presente(),
				cliente.getMensaje_titulo(),
				cliente.getMensaje_detalles(),
				cliente.getMensaje_enlace_presente(),
				cliente.getMensaje_enlace_nombre(),
				cliente.getMensaje_enlace())
			: new ClienteResultado(
				false,
				null,
				null,
				cliente.getMensaje_presente(),
				cliente.getMensaje_titulo(),
				cliente.getMensaje_detalles(),
				cliente.getMensaje_enlace_presente(),
				cliente.getMensaje_enlace_nombre(),
				cliente.getMensaje_enlace()
		);

		metricasService.contarAcceso(plataforma);

		return resultado;
	}

}
