package com.quimify.api.organic;

import com.quimify.api.error.ErrorService;
import com.quimify.api.molecularmass.MolecularMassService;
import com.quimify.api.metrics.MetricsService;
import com.quimify.api.notfoundquery.NotFoundQueryService;
import com.quimify.organic.OrganicFactory;
import com.quimify.organic.Organic;
import com.quimify.organic.components.Group;
import com.quimify.organic.components.Substituent;
import com.quimify.organic.molecules.openchain.OpenChain;
import com.quimify.organic.molecules.openchain.Simple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

// This class implements the logic behind HTTP methods in "/organic".

@Service
class OrganicService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    // TODO classifier, corrections

    // TODO: Sugerencia con regex "^[a-zA-Z].*oxi.*o$" (afinar más) de "1-...", para "butoxidecano" por ejemplo

    @Autowired
    PubChemComponent pubChemComponent;

    @Autowired
    MolecularMassService molecularMassService;

    @Autowired
    NotFoundQueryService notFoundQueryService;

    @Autowired
    ErrorService errorService;

    @Autowired
    MetricsService metricsService;

    // Constants:

    static final int carbonInputCode = -1;

    // TODO "EX" -> "HEX" ?

    // Client:

    OrganicResult getFromName(String name) {
        OrganicResult organicResult;

        try {
            Optional<Organic> organic = OrganicFactory.getFromName(name);

            if (organic.isPresent()) {
                organicResult = resolvePropertiesOf(organic.get());

                if (organic.get().getStructureException() != null) {
                    // TODO classifier & menu suggestion
                    Exception exception = organic.get().getStructureException();
                    errorService.log("Exception solving name: " + name, exception.toString(), getClass());
                }
            }
            else {
                // TODO classifier & menu suggestion
                logger.warn("Couldn't solve organic \"" + name + "\".");
                organicResult = OrganicResult.notFound();
            }
        } catch (Exception exception) {
            // TODO classifier & menu suggestion
            errorService.log("Exception solving name: " + name, exception.toString(), getClass());
            organicResult = OrganicResult.notFound();
        } catch (StackOverflowError error) {
            errorService.log("StackOverflow error", name, getClass());
            organicResult = OrganicResult.notFound();
        }

        if (!organicResult.isFound())
            notFoundQueryService.log(name, getClass());

        metricsService.organicFromNameQueried(organicResult.isFound());

        return organicResult;
    }

    OrganicResult getFromStructure(int[] inputSequence) {
        OrganicResult organicResult;

        String sequenceToString = Arrays.toString(inputSequence); // For logging purposes

        try {
            OpenChain openChain = getOpenChainFromStructure(inputSequence);
            Organic organic = OrganicFactory.getFromOpenChain(openChain);

            organicResult = resolvePropertiesOf(organic);
        } catch (Exception exception) {
            errorService.log("Exception naming: " + sequenceToString, exception.toString(), getClass());
            organicResult = OrganicResult.notFound();
        }

        if (!organicResult.isFound())
            notFoundQueryService.log(sequenceToString, getClass());

        metricsService.organicFromStructureQueried(organicResult.isFound());

        return organicResult;
    }

    // Private:

    private static OpenChain getOpenChainFromStructure(int[] inputSequence) {
        OpenChain openChain = new Simple();

        for (int i = 0; i < inputSequence.length; i++) {
            if (inputSequence[i] == carbonInputCode) {
                openChain.bondCarbon();
                continue;
            }

            Group group = Group.values()[inputSequence[i]];

            if (group == Group.radical) {
                boolean iso = inputSequence[++i] == 1;
                int carbonCount = inputSequence[++i];

                openChain = openChain.bond(Substituent.radical(carbonCount, iso));
            }
            else openChain = openChain.bond(group);
        }

        return openChain;
    }

    private OrganicResult resolvePropertiesOf(Organic organic) {
        if (organic.getSmiles() == null)
            return new OrganicResult(organic.getName(), organic.getStructure(), null, null);

        pubChemComponent.resolveCompound(organic.getSmiles());

        String url2D = pubChemComponent.getUrl2D();

        Optional<Float> molecularMass = Optional.empty();

        if (organic.getStructure() != null)
            molecularMass = molecularMassService.get(organic.getStructure());

        if (molecularMass.isEmpty())
            molecularMass = pubChemComponent.getMolecularMass();

        return new OrganicResult(organic.getName(), organic.getStructure(), molecularMass.orElse(null), url2D);
    }

}
