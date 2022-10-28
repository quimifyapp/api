package com.quimify.api.organic;

import com.quimify.api.molecular_mass.MolecularMassService;
import com.quimify.api.metrics.MetricsService;
import com.quimify.organic.OrganicFactory;
import com.quimify.organic.OrganicResult;
import com.quimify.organic.components.FunctionalGroup;
import com.quimify.organic.components.Substituent;
import com.quimify.organic.compounds.open_chain.Ether;
import com.quimify.organic.compounds.open_chain.OpenChain;
import com.quimify.organic.compounds.open_chain.Simple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

// Esta clase procesa los compuestos orgánicos.

@Service
class OrganicService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	MolecularMassService molecularMassService; // Procesos de las masas moleculares

	@Autowired
	MetricsService metricsService; // Procesos de las metricas diarias

	protected static final int carbonInputCode = -1;

	// CLIENTE -----------------------------------------------------------------------

	protected OrganicResult getFromName(String name, Boolean picture) {
		OrganicResult organicResult = OrganicFactory.getFromName(name);

		if(organicResult.getEncontrado()) {
			if(organicResult.getFormula() != null)
				organicResult.setMasa(molecularMassService.tryCalculateMolecularMassOf(organicResult.getFormula()));

		}
		else logger.warn("No se ha encontrado el orgánico \"" + name + "\".");

		metricsService.contarFormularOrganico(organicResult.getEncontrado(), picture);

		return organicResult;
	}

	protected OrganicResult getFromStructure(int[] inputSequence) {
		try {
			OpenChain openChain = getOpenChainFromStructure(inputSequence);

			OrganicResult organicResult = OrganicFactory.getFromOpenChain(openChain);

			if(organicResult.getEncontrado())
				organicResult.setMasa(molecularMassService.tryCalculateMolecularMassOf(organicResult.getFormula()));


			metricsService.contarNombrarOrganicoAbiertoBuscado();

			return organicResult;
		}
		catch (Exception exception) {
			metricsService.countOrganicsFailedFromStructure();
			logger.error("Excepción al nombrar [" + Arrays.toString(inputSequence) + "]: " + exception + ".");
			return OrganicFactory.organicNotFound;
		}

	}

	// PRIVATE -----------------------------------------------------------------------

	private static OpenChain getOpenChainFromStructure(int[] inputSequence) {
		OpenChain openChain = new Simple();

		for(int i = 0; i < inputSequence.length; i++) {
			if(inputSequence[i] != carbonInputCode) {
				FunctionalGroup groupElection = openChain.getOrderedBondableGroups().get(inputSequence[i]);

				if (groupElection != FunctionalGroup.radical) {
					openChain.bond(groupElection);

					if (groupElection == FunctionalGroup.ether) {
						assert openChain instanceof Simple; // Yes, it is...
						openChain = new Ether((Simple) openChain);
					}
				}
				else { // Radical
					boolean isIso = inputSequence[++i] == 1;
					int carbonCount = inputSequence[++i];
					openChain.bond(new Substituent(carbonCount, isIso));
				}
			}
			else openChain.bondCarbon();
		}

		return openChain;
	}

}
