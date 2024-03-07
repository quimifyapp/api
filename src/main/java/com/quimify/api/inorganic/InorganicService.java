package com.quimify.api.inorganic;

import com.quimify.api.classification.Classification;
import com.quimify.api.classification.ClassificationService;
import com.quimify.api.correction.CorrectionService;
import com.quimify.api.error.ErrorService;
import com.quimify.api.molecularmass.MolecularMassService;
import com.quimify.api.notfoundquery.NotFoundQueryService;
import com.quimify.api.settings.SettingsService;
import com.quimify.api.metrics.MetricsService;
import com.quimify.api.utils.Normalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

// This class implements the logic behind HTTP methods in "/inorganic".

@Service
class InorganicService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    InorganicRepository inorganicRepository;

    @Autowired
    CacheComponent cacheComponent;

    @Autowired
    ClassificationService classificationService;

    @Autowired
    CorrectionService correctionService;

    @Autowired
    CompletionComponent completionComponent;

    @Autowired
    WebSearchComponent webSearchComponent;

    @Autowired
    WebParseComponent webParseComponent;

    @Autowired
    MolecularMassService molecularMassService;

    @Autowired
    SettingsService settingsService;

    @Autowired
    NotFoundQueryService notFoundQueryService;

    @Autowired
    MetricsService metricsService;

    @Autowired
    ErrorService errorService;

    // Client:

    InorganicResult search(String input) {
        Optional<InorganicResult> stored = memorySearch(input);

        if (stored.isPresent())
            return stored.get();

        Optional<InorganicResult> corrected = correctionSearch(input);

        if (corrected.isPresent())
            return corrected.get();

        // TODO by similarity
        // TODO metricsService.inorganicSimilaritySearched(...);

        Optional<InorganicResult> classified = classificationSearch(input);

        if (classified.isPresent())
            return classified.get();

        return deepSearch(input);
    }

    InorganicResult deepSearch(String input) {
        Optional<String> url = webSearchComponent.search(input);

        if (url.isEmpty()) {
            metricsService.inorganicDeepSearchFailed();
            notFoundQueryService.log(input, getClass());

            return InorganicResult.notFound();
        }

        Optional<InorganicModel> parsedInorganic = webParseComponent.tryParse(url.get());

        if (parsedInorganic.isEmpty()) {
            metricsService.inorganicDeepSearchFailed();
            notFoundQueryService.log(input, getClass());

            return InorganicResult.notFound();
        }

        Optional<InorganicModel> searchedInMemory = fetch(parsedInorganic.get().getFormula());

        if (searchedInMemory.isPresent()) {
            metricsService.inorganicDeepSearchFound();
            logger.warn("Parsed inorganic \"" + input + "\" was already: " + searchedInMemory.get());

            return new InorganicResult(searchedInMemory.get()); // TODO with suggestion + evaluate max similarity
        }

        return learnParsedInorganic(input, parsedInorganic.get()); // TODO with suggestion + evaluate max similarity
    }

    String complete(String input) {
        String completion = completionComponent.tryComplete(input);

        if (completion.equals(CompletionComponent.notFound)) {
            String correctedInput = correctionService.correct(input, true);

            if (!input.equals(correctedInput))
                completion = completionComponent.tryComplete(correctedInput);
        }

        return completion;
    }

    InorganicResult completionSearch(String completion) {
        InorganicResult inorganicResult;

        Optional<InorganicModel> searchedInMemory = fetch(completion);

        if (searchedInMemory.isPresent())
            inorganicResult = new InorganicResult(searchedInMemory.get());
        else {
            errorService.log("Completion not in DB", completion, getClass());
            notFoundQueryService.log(completion, getClass());

            inorganicResult = InorganicResult.notFound();
        }

        metricsService.inorganicCompleted();
        metricsService.inorganicSearched(inorganicResult.isFound());

        return inorganicResult;
    }

    // Private:

    private Optional<InorganicResult> memorySearch(String input) {
        Optional<InorganicModel> searchedInMemory = fetch(input);

        metricsService.inorganicSearched(searchedInMemory.isPresent());

        if (searchedInMemory.isEmpty()) {
            logger.warn("Inorganic not in DB: \"" + input + "\".");
            return Optional.empty();
        }

        return Optional.of(new InorganicResult(searchedInMemory.get()));
    }

    private Optional<InorganicResult> correctionSearch(String input) {
        Optional<InorganicResult> inorganicResult = Optional.empty();

        String correctedInput = correctionService.correct(input, false);

        if (!input.equals(correctedInput)) {
            Optional<InorganicModel> searchedInMemory = fetch(correctedInput);

            if (searchedInMemory.isPresent()) {
                logger.info("Found corrected \"" + correctedInput + "\" from: \"" + input + "\".");
                inorganicResult = Optional.of(new InorganicResult(searchedInMemory.get(), correctedInput));
            }
        }

        if (inorganicResult.isEmpty()) {
            correctedInput = "ácido " + correctedInput;

            Optional<InorganicModel> searchedInMemory = fetch(correctedInput);

            if (searchedInMemory.isPresent()) {
                logger.info("Found corrected \"" + correctedInput + "\" from: \"" + input + "\".");
                inorganicResult = Optional.of(new InorganicResult(searchedInMemory.get(), correctedInput));
            }
        }

        metricsService.inorganicCorrectionSearched(inorganicResult.isPresent());

        return inorganicResult;
    }

    private Optional<InorganicResult> classificationSearch(String input) {
        Optional<Classification> result = classificationService.classify(input);

        metricsService.inorganicClassificationSearched(result.isPresent());

        if (result.isPresent() && !classificationService.isInorganic(result.get()))
            return Optional.of(InorganicResult.classification(result.get()));

        return Optional.empty();
    }

    private Optional<InorganicModel> fetch(String input) {
        Optional<Integer> id = cacheComponent.find(Normalizer.get(input));

        if (id.isEmpty())
            return Optional.empty();

        Optional<InorganicModel> inorganicModel = inorganicRepository.findById(id.get());

        if (inorganicModel.isPresent())
            inorganicModel.get().countSearch();
        else logger.warn("Discrepancy between DB and cached ID: " + id.get());

        return inorganicModel;
    }

    private InorganicResult learnParsedInorganic(String input, InorganicModel parsedInorganic) {
        if (!hasInorganicName(parsedInorganic)) {
            logger.warn("Parsed inorganic " + parsedInorganic + " doesn't look like an inorganic.");
            metricsService.inorganicDeepSearchFailed();
            notFoundQueryService.log(input, getClass());

            return InorganicResult.notFound();
        }

        Optional<Float> newMolecularMass = molecularMassService.get(parsedInorganic.getFormula());

        if (newMolecularMass.isPresent()) {
            String molecularMass = String.format("%.2f", newMolecularMass.get()).replace(",", ".");
            parsedInorganic.setMolecularMass(molecularMass);
        }

        inorganicRepository.save(parsedInorganic);
        cacheComponent.add(parsedInorganic);

        metricsService.inorganicDeepSearchLearned();

        logger.warn("Learned inorganic: " + parsedInorganic);

        if (parsedInorganic.toString().contains("peróxido")) // Any of its names
            logger.warn("Learned peroxide might need manual correction: " + parsedInorganic);

        return new InorganicResult(parsedInorganic);
    }

    private boolean hasInorganicName(InorganicModel inorganicModel) {
        String name = inorganicModel.getStockName();

        if (name == null)
            name = inorganicModel.getSystematicName();

        if (name == null)
            name = inorganicModel.getTraditionalName();

        Optional<Classification> classifierResult = classificationService.classify(name);

        return classifierResult.isEmpty() || classificationService.isInorganic(classifierResult.get());
    }

}
