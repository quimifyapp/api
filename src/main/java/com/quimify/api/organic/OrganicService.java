package com.quimify.api.organic;

import com.quimify.api.classification.Classification;
import com.quimify.api.classification.ClassificationService;
import com.quimify.api.correction.CorrectionService;
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

    @Autowired
    ClassificationService classificationService;

    @Autowired
    CorrectionService correctionService;

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

    // Client:

    OrganicResult getFromName(String input) {
        Optional<OrganicResult> result = tryGetFromName(input);

        if (result.isEmpty())
            result = getFromCorrectedName(input);

        if (result.isEmpty())
            result = Optional.of(OrganicResult.notFound());

        if (!result.get().isFound() || result.get().getStructure() == null)
            result = Optional.of(classifyName(input, result.get()));

        if (!result.get().isFound())
            logger.warn("Couldn't solve organic \"{}\".", input);

        metricsService.organicFromNameQueried(result.get().isFound());

        return result.get();
    }

    OrganicResult tryGetFromStructure(int[] inputSequence) {
        OrganicResult organicResult;

        String sequenceToString = Arrays.toString(inputSequence); // For logging purposes

        try {
            OpenChain openChain = getFromStructure(inputSequence);
            Organic organic = OrganicFactory.getFromOpenChain(openChain);

            organicResult = processSolved(organic);
        } catch (Exception exception) {
            organicResult = OrganicResult.notFound();
            errorService.log("Exception naming: " + sequenceToString, exception.toString(), getClass());
        }

        if (!organicResult.isFound())
            notFoundQueryService.log(sequenceToString, getClass());

        metricsService.organicFromStructureQueried(organicResult.isFound());

        return organicResult;
    }

    // Private:

    Optional<OrganicResult> getFromCorrectedName(String input) {
        Optional<OrganicResult> organicResult = Optional.empty();

        String correctedInput = correctionService.correct(input, false);

        if (!input.equals(correctedInput)) {
            organicResult = tryGetFromName(correctedInput);

            if (organicResult.isPresent() && organicResult.get().isFound()) {
                organicResult.get().setSuggestion(correctedInput);
                logger.info("Successfully corrected \"{}\" from: \"{}\".", correctedInput, input);
            }
        }

        // TODO temp fix:

        if (organicResult.isEmpty()) {
            String acidCorrectedInput = "ácido " + correctedInput;

            organicResult = tryGetFromName(acidCorrectedInput);

            if (organicResult.isPresent()) {
                organicResult.get().setSuggestion(acidCorrectedInput);
                logger.info("Successfully corrected \"{}\" from: \"{}\".", acidCorrectedInput, input);
            }
        }

        // TODO temp fix:

        if (organicResult.isEmpty()) {
            String locatorCorrectedInput = "1-" + correctedInput;

            organicResult = tryGetFromName(locatorCorrectedInput);

            if (organicResult.isPresent()) {
                organicResult.get().setSuggestion(locatorCorrectedInput);
                logger.info("Successfully corrected \"{}\" from: \"{}\".", locatorCorrectedInput, input);
            }
        }

        return organicResult;
    }

    Optional<OrganicResult> tryGetFromName(String name) {
        Optional<OrganicResult> result = Optional.empty();

        try {
            Optional<Organic> organic = OrganicFactory.getFromName(name);

            if (organic.isPresent()) {
                result = Optional.of(processSolved(organic.get()));

                if (organic.get().getStructureException() != null) {
                    Exception exception = organic.get().getStructureException();
                    errorService.log("Exception solving name: " + name, exception.toString(), getClass());
                }
            }
        } catch (Exception exception) {
            result = Optional.of(OrganicResult.notFound());
            errorService.log("Exception solving name: " + name, exception.toString(), getClass());
        } catch (StackOverflowError error) {
            result = Optional.of(OrganicResult.notFound());
            errorService.log("StackOverflow error", name, getClass());
        }

        return result;
    }

    private OrganicResult classifyName(String input, OrganicResult organicResult) {
        Optional<Classification> classification = classificationService.classify(input);

        if (classification.isPresent() && classification.get() != Classification.organicName)
            organicResult.setClassification(classification.get());

        return organicResult;
    }

    private OpenChain getFromStructure(int[] inputSequence) {
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

    private OrganicResult processSolved(Organic organic) {
        OrganicResult result = new OrganicResult(organic.getName(), organic.getStructure());

        if (organic.getSmiles() != null)
            pubChemComponent.setProperties(result, organic.getSmiles());

        if (organic.getStructure() != null) {
            // Calculated molecular mass is preferred for consistency over PubChem's
            Optional<Float> calculatedMolecularMass = molecularMassService.get(organic.getStructure());

            calculatedMolecularMass.ifPresent(result::setMolecularMass);
        }

        return result;
    }

}
