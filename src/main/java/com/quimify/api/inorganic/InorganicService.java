package com.quimify.api.inorganic;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quimify.api.classification.Classification;
import com.quimify.api.classification.ClassificationService;
import com.quimify.api.correction.CorrectionService;
import com.quimify.api.error.ErrorService;
import com.quimify.api.metrics.MetricsService;
import com.quimify.api.molecularmass.MolecularMassService;
import com.quimify.api.notfoundquery.NotFoundQueryService;
import com.quimify.api.utils.Normalizer;

@Service
public abstract class InorganicService<InorganicModel extends com.quimify.api.inorganic.InorganicModel> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected abstract InorganicRepository<InorganicModel> getRepository();

    protected abstract CacheComponent<InorganicModel> getCacheComponent();

    protected abstract ClassificationService getClassificationService();

    protected abstract CorrectionService getCorrectionService();

    protected abstract CompletionComponent<InorganicModel> getCompletionComponent();

    protected abstract WebParseComponent<InorganicModel> getWebParseComponent();

    @Autowired
    WebSearchComponent webSearchComponent;

    @Autowired
    MolecularMassService molecularMassService;

    @Autowired
    NotFoundQueryService notFoundQueryService;

    @Autowired
    MetricsService metricsService;

    @Autowired
    ErrorService errorService;

    // Internal:

    public boolean isHealthy() {
        Optional<Integer> cachedId = getCacheComponent().find(Normalizer.get("h2o"));
        return cachedId.isPresent();
    }

    // Client:

    protected InorganicResult search(String input) {
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

    protected InorganicResult deepSearch(String input) {
        Optional<String> url = webSearchComponent.search(input);

        if (url.isEmpty()) {
            metricsService.inorganicDeepSearchFailed();
            notFoundQueryService.log(input, getClass());

            return InorganicResult.notFound();
        }

        Optional<InorganicModel> parsedInorganic = getWebParseComponent().tryParse(url.get());

        if (parsedInorganic.isEmpty()) {
            metricsService.inorganicDeepSearchFailed();
            notFoundQueryService.log(input, getClass());

            return InorganicResult.notFound();
        }

        Optional<InorganicModel> searchedInMemory = fetch(parsedInorganic.get().getFormula());

        if (searchedInMemory.isPresent()) {
            metricsService.inorganicDeepSearchFound();
            logger.warn("Parsed inorganic \"{}\" was already: {}", input, searchedInMemory.get());

            return new InorganicResult(searchedInMemory.get()); // TODO with suggestion + evaluate max similarity
        }

        Optional<Float> newMolecularMass = molecularMassService.get(parsedInorganic.get().getFormula());

        if (newMolecularMass.isPresent()) {
            String molecularMass = String.format("%.2f", newMolecularMass.get()).replace(",", ".");
            parsedInorganic.get().setMolecularMass(molecularMass);
        }

        learnParsed(parsedInorganic.get());

        return new InorganicResult(parsedInorganic.get()); // TODO with suggestion + evaluate max similarity
    }

    protected String complete(String input) {
        String completion = getCompletionComponent().tryComplete(input);

        if (completion.equals(CompletionComponent.notFound)) {
            String correctedInput =getCorrectionService().correct(input, true);

            if (!input.equals(correctedInput))
                completion = getCompletionComponent().tryComplete(correctedInput);
        }

        return completion;
    }

    protected InorganicResult completionSearch(String completion) {
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
            logger.warn("Inorganic not in DB: \"{}\".", input);
            return Optional.empty();
        }

        return Optional.of(new InorganicResult(searchedInMemory.get()));
    }

    private Optional<InorganicResult> correctionSearch(String input) {
        Optional<InorganicResult> inorganicResult = Optional.empty();

        String correctedInput = getCorrectionService().correct(input, false);

        if (!input.equals(correctedInput)) {
            Optional<InorganicModel> searchedInMemory = fetch(correctedInput);

            if (searchedInMemory.isPresent()) {
                logger.info("Successfully corrected \"{}\" from: \"{}\".", correctedInput, input);
                inorganicResult = Optional.of(new InorganicResult(searchedInMemory.get(), correctedInput));
            }
        }

        // TODO temp fix:

        if (inorganicResult.isEmpty()) {
            String acidCorrectedInput = "ácido " + correctedInput;

            Optional<InorganicModel> searchedInMemory = fetch(acidCorrectedInput);

            if (searchedInMemory.isPresent()) {
                logger.info("Successfully corrected \"{}\" from: \"{}\".", acidCorrectedInput, input);
                inorganicResult = Optional.of(new InorganicResult(searchedInMemory.get(), acidCorrectedInput));
            }
        }

        // TODO temp fix:

        if (inorganicResult.isEmpty()) {
            String monoCorrectedInput = "mono" + correctedInput;

            monoCorrectedInput = monoCorrectedInput.replace("monoo", "mono");

            Optional<InorganicModel> searchedInMemory = fetch(monoCorrectedInput);

            if (searchedInMemory.isPresent()) {
                logger.info("Successfully corrected \"{}\" from: \"{}\".", monoCorrectedInput, input);
                inorganicResult = Optional.of(new InorganicResult(searchedInMemory.get(), monoCorrectedInput));
            }
        }

        metricsService.inorganicCorrectionSearched(inorganicResult.isPresent());

        return inorganicResult;
    }

    private Optional<InorganicResult> classificationSearch(String input) {
        Optional<Classification> result = getClassificationService().classify(input);

        metricsService.inorganicClassificationSearched(result.isPresent());

        if (result.isEmpty() || getClassificationService().isInorganic(result.get()))
            return Optional.empty();

        return Optional.of(InorganicResult.classification(result.get()));
    }

    private Optional<InorganicModel> fetch(String input) {
        Optional<Integer> id = getCacheComponent().find(Normalizer.get(input));

        if (id.isEmpty())
            return Optional.empty();

        Optional<InorganicModel> inorganicModel = getRepository().findById(id.get());

        if (inorganicModel.isPresent())
            inorganicModel.get().countSearch();
        else
            logger.warn("Discrepancy between DB and cached ID: {}", id.get());

        return inorganicModel;
    }

    private void learnParsed(InorganicModel parsedInorganic) {
        getRepository().save(parsedInorganic);
        getCacheComponent().save(parsedInorganic);

        metricsService.inorganicDeepSearchLearned();
        logger.warn("Learned inorganic: {}", parsedInorganic);

        if (parsedInorganic.toString().contains("peróxido")) // Any of its names
            logger.warn("Learned peroxide might need manual correction: {}", parsedInorganic);
    }

}
