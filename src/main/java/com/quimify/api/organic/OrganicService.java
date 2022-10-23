package com.quimify.api.organic;

import com.quimify.api.masa_molecular.MasaMolecularResultado;
import com.quimify.api.masa_molecular.MasaMolecularService;
import com.quimify.api.metricas.MetricasService;
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

// Esta clase procesa los compuestos orgánicos.

@Service
public class OrganicService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	MasaMolecularService masaMolecularService; // Procesos de las masas moleculares

	@Autowired
	MetricasService metricasService; // Procesos de las metricas diarias

	public static final int carbonInputCode = -1;

	// CLIENTE -----------------------------------------------------------------------

	public OrganicResult getFromName(String name, Boolean picture) {
		OrganicResult organicResult = OrganicFactory.getFromName(name);

		if(organicResult.getEncontrado()) {
			if(organicResult.getFormula() != null)
				addMolecularMassIfMissing(organicResult);
		}
		else logger.warn("No se ha encontrado el orgánico \"" + name + "\".");

		metricasService.contarFormularOrganico(organicResult.getEncontrado(), picture);

		return organicResult;
	}

	public OrganicResult getFromStructure(int[] inputSequence) {
		OpenChain openChain = getOpenChainFromStructure(inputSequence);

		OrganicResult organicResult = OrganicFactory.getFromOpenChain(openChain);

		if(organicResult.getEncontrado())
			addMolecularMassIfMissing(organicResult);

		metricasService.contarNombrarOrganicoAbiertoBuscado();

		return organicResult;
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

	private void addMolecularMassIfMissing(OrganicResult organicResult) {
		if(organicResult.getMasa() == null) {
			MasaMolecularResultado masaMolecular = masaMolecularService.tryMasaMolecularDe(organicResult.getFormula());

			if(masaMolecular.getEncontrado())
				organicResult.setMasa(masaMolecular.getMasa());
		}
	}

}
