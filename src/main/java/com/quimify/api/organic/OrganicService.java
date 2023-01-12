package com.quimify.api.organic;

import com.quimify.api.error.ErrorService;
import com.quimify.api.molecular_mass.MolecularMassService;
import com.quimify.api.metrics.MetricsService;
import com.quimify.organic.OrganicFactory;
import com.quimify.organic.OrganicResult;
import com.quimify.organic.components.Group;
import com.quimify.organic.components.Substituent;
import com.quimify.organic.molecules.open_chain.OpenChain;
import com.quimify.organic.molecules.open_chain.Simple;
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
	MolecularMassService molecularMassService; // Molecular masses logic

	@Autowired
	ErrorService errorService; // API errors logic

	@Autowired
	MetricsService metricsService; // Daily metrics logic

	protected static final int carbonInputCode = -1;

	// CLIENT ------------------------------------------------------------------------

	protected OrganicResult getFromName(String name, Boolean picture) {
		OrganicResult organicResult = OrganicFactory.getFromName(name);

		if(organicResult.getPresent()) {
			if(organicResult.getStructure() != null)
				organicResult.setMolecularMass(
						molecularMassService.tryMolecularMassOf(organicResult.getStructure()));
		}
		else logger.warn("No se ha encontrado el orgánico \"" + name + "\".");

		metricsService.contarFormularOrganico(organicResult.getPresent(), picture);

		return organicResult;
	}

	protected OrganicResult getFromStructure(int[] inputSequence) {
		try {
			OpenChain openChain = getOpenChainFromStructure(inputSequence);

			OrganicResult organicResult = OrganicFactory.getFromOpenChain(openChain);

			if(organicResult.getPresent())
				organicResult.setMolecularMass(molecularMassService.tryMolecularMassOf(organicResult.getStructure()));

			metricsService.contarNombrarOrganicoAbiertoBuscado();

			return organicResult;
		}
		catch (Exception exception) {
			String sequenceToString = Arrays.toString(inputSequence);
			errorService.saveError("Exception naming: " + sequenceToString, exception.toString(), this.getClass());

			metricsService.countOrganicsFailedFromStructure();

			return OrganicFactory.organicNotFound;
		}
	}

	// -------------------------------------------------------------------------------

	private static OpenChain getOpenChainFromStructure(int[] inputSequence) {
		OpenChain openChain = new Simple();

		for(int i = 0; i < inputSequence.length; i++) {
			if (inputSequence[i] == carbonInputCode) {
				openChain.bondCarbon();
				continue;
			}

			Group groupElection = openChain.getBondableGroups().get(inputSequence[i]);

			if (groupElection != Group.radical)
				openChain = openChain.bond(groupElection);
			else {
				boolean isIso = inputSequence[++i] == 1;
				int carbonCount = inputSequence[++i];
				openChain = openChain.bond(new Substituent(carbonCount, isIso));
			}
		}

		return openChain;
	}

}
