package com.quimify.api.organic;

import com.quimify.api.masa_molecular.MasaMolecularService;
import com.quimify.api.metricas.MetricasService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Esta clase procesa los compuestos orgánicos.

@Service
public class OrganicService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	MasaMolecularService masaMolecularService; // Procesos de las masas moleculares

	@Autowired
	MetricasService metricasService; // Procesos de las metricas diarias

	// CLIENTE -----------------------------------------------------------------------

	public OrganicResult getFromName(String name, Boolean picture) {
		OrganicResult organicResult = OrganicFactory.getFromName(name);

		if(!organicResult.getEncontrado())
			logger.warn("No se ha encontrado el orgánico \"" + name + "\".");

		metricasService.contarFormularOrganico(organicResult.getEncontrado(), picture);

		return organicResult;
	}

	public OrganicResult getFromStructure(int[] inputSequence) {
		OrganicResult organicResult = OrganicFactory.getFromStructure(inputSequence);

		metricasService.contarNombrarOrganicoSimpleBuscado();

		return organicResult;
	}

}
