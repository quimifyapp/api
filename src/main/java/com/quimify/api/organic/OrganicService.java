package com.quimify.api.organic;

import com.quimify.api.error.ErrorService;
import com.quimify.api.molecular_mass.MolecularMassService;
import com.quimify.api.metrics.MetricsService;
import com.quimify.organic.OrganicFactory;
import com.quimify.organic.Organic;
import com.quimify.organic.components.Group;
import com.quimify.organic.components.Substituent;
import com.quimify.organic.molecules.open_chain.OpenChain;
import com.quimify.organic.molecules.open_chain.Simple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

// This class implements the logic behind HTTP methods in "/organic".

@Service
class OrganicService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	PubChemComponent pubChemComponent; // PubChem API logic

	@Autowired
	MolecularMassService molecularMassService; // Molecular masses logic

	@Autowired
	ErrorService errorService; // API errors logic

	@Autowired
	MetricsService metricsService; // Daily metrics logic

	// Constants:

	protected static final int carbonInputCode = -1;

	// Client:

	protected OrganicResult getFromName(String name, Boolean picture) {
		OrganicResult organicResult;

		try {
			Optional<Organic> organic = OrganicFactory.getFromName(name);

			if(organic.isPresent())
				organicResult = resolvePropertiesOf(organic.get());
			else {
				logger.warn("Couldn't solve organic \"" + name + "\".");
				organicResult = OrganicResult.notFound;
			}
		} catch (Exception exception) {
			errorService.saveError("Exception solving name: " + name, exception.toString(), this.getClass());
			organicResult = OrganicResult.notFound;
		}

		metricsService.countOrganicSearchedFromName(organicResult.isPresent(), picture);

		return organicResult;
	}

	protected OrganicResult getFromStructure(int[] inputSequence) {
		OrganicResult organicResult;

		try {
			OpenChain openChain = getOpenChainFromStructure(inputSequence);
			Organic organic = OrganicFactory.getFromOpenChain(openChain);
			
			organicResult = resolvePropertiesOf(organic);
		}
		catch (Exception exception) {
			String sequenceToString = Arrays.toString(inputSequence);
			errorService.saveError("Exception naming: " + sequenceToString, exception.toString(), this.getClass());

			organicResult = OrganicResult.notFound;
		}

		metricsService.countOrganicSearchedFromStructure(organicResult.isPresent());

		return organicResult;
	}

	// Private:

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
				boolean iso = inputSequence[++i] == 1;
				int carbonCount = inputSequence[++i];

				openChain = openChain.bond(new Substituent(carbonCount, iso));
			}
		}

		return openChain;
	}

	private OrganicResult resolvePropertiesOf(Organic organic) {
		if(organic.getSmiles() == null)
			return new OrganicResult(organic.getName(), organic.getStructure(), null, null);

		pubChemComponent.resolveCompound(organic.getSmiles());

		String url2D = pubChemComponent.getUrl2D();

		Optional<Float> molecularMass = Optional.empty();

		if(organic.getStructure() != null)
			molecularMass = molecularMassService.get(organic.getStructure());

		if(molecularMass.isEmpty())
			molecularMass = pubChemComponent.getMolecularMass();

		return new OrganicResult(organic.getName(), organic.getStructure(), molecularMass.orElse(null), url2D);
	}

}
