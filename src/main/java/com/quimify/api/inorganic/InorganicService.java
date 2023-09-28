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
    InorganicRepository inorganicRepository; // DB connection

    @Autowired
    CacheComponent cacheComponent;

    @Autowired
    ClassificationService classificationService; // TODO run all over DB

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

    //if(!inorganicResult.isPresent()) TODO logs, metrics...
    //        notFoundQueryService.log(input, getClass());

    //metricsService.inorganicSearched(inorganicResult.isPresent());

    InorganicResult search(String input) {
        Optional<InorganicModel> searchedInMemory = fetch(input);

        if (searchedInMemory.isPresent())
            return new InorganicResult(searchedInMemory.get());

        Optional<Classification> classifierResult = classificationService.classify(input);

        if (classifierResult.isPresent() && !classificationService.isInorganic(classifierResult.get()))
            return new InorganicResult(classifierResult.get().toString());

        return smartSearch(input);
    }

    InorganicResult smartSearch(String input) { // TODO re-structure
        String correctedInput = correctionService.correct(input);

        if (!input.equals(correctedInput)) {
            Optional<InorganicModel> searchedInMemory = fetch(correctedInput);

            if (searchedInMemory.isPresent())
                return new InorganicResult(searchedInMemory.get(), correctedInput);
        }

        Optional<InorganicModel> searchedInMemory = fetch("ácido " + correctedInput);

        if (searchedInMemory.isPresent())
            return new InorganicResult(searchedInMemory.get(), correctedInput);

        return enrichedSearch(input); // TODO similarity instead
    }

    InorganicResult enrichedSearch(String input) {
        Optional<String> url = webSearchComponent.search(input);

        if (url.isEmpty())
            return InorganicResult.notFound();

        return parseInorganic(url.get(), input);
    }

    String complete(String input) {
        String completion = completionComponent.tryComplete(input);

        if (completion.equals(CompletionComponent.notFound)) {
            String correctedInput = correctionService.correct(input);

            if (!input.equals(correctedInput))
                completion = completionComponent.tryComplete(correctedInput);
        }

        return completion;
    }

    InorganicResult searchFromCompletion(String completion) {
        InorganicResult inorganicResult;

        Optional<InorganicModel> searchedInMemory = fetch(completion);

        if (searchedInMemory.isPresent())
            inorganicResult = new InorganicResult(searchedInMemory.get());
        else {
            errorService.log("Completion not in DB", completion, getClass());
            inorganicResult = InorganicResult.notFound();
        }

        metricsService.inorganicCompleted();
        metricsService.inorganicSearched(inorganicResult.isFound()); // TODO here?

        return inorganicResult;
    }

    // Private:

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

    private InorganicResult parseInorganic(String url, String input) {
        Optional<InorganicModel> parsedInorganic = webParseComponent.tryParse(url);

        if (parsedInorganic.isEmpty())
            return InorganicResult.notFound();

        // Check if it was already in the DB:

        Optional<InorganicModel> searchedInMemory = fetch(parsedInorganic.get().getFormula());

        if (searchedInMemory.isPresent()) {
            logger.warn("Parsed inorganic \"" + input + "\" was already: " + searchedInMemory.get());
            return new InorganicResult(searchedInMemory.get()); // TODO deduce suggestion
        }

        return learnParsedInorganic(parsedInorganic.get()); // TODO deduce suggestion
    }

    private InorganicResult learnParsedInorganic(InorganicModel parsedInorganic) {
        Optional<Float> newMolecularMass = molecularMassService.get(parsedInorganic.getFormula());

        if (newMolecularMass.isPresent()) {
            String molecularMass = String.format("%.2f", newMolecularMass.get()).replace(",", ".");
            parsedInorganic.setMolecularMass(molecularMass);
        }

        inorganicRepository.save(parsedInorganic);
        cacheComponent.add(parsedInorganic);

        metricsService.inorganicLearned();
        logger.info("Learned inorganic: " + parsedInorganic);

        return new InorganicResult(parsedInorganic);
    }

}
