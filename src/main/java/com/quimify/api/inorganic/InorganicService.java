package com.quimify.api.inorganic;

import com.quimify.api.classifier.ClassifierResult;
import com.quimify.api.classifier.ClassifierService;
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
public class InorganicService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    InorganicRepository inorganicRepository; // DB connection

    @Autowired
    CacheComponent cacheComponent;

    @Autowired
    ClassifierService classifierService;

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

    // Constants:

    private static final Map<ClassifierResult, String> classifierResultToMenuSuggestion = Map.of( // TODO rename
            ClassifierResult.organicFormula, "organic-naming",
            ClassifierResult.organicName, "organic-finding-formula"
    );

    //if(!inorganicResult.isPresent()) TODO logs, metrics...
    //        notFoundQueryService.log(input, getClass());

    //metricsService.inorganicSearched(inorganicResult.isPresent());

    // Client:

    InorganicResult search(String input) {
        Optional<InorganicModel> searchedInMemory = fetch(input);

        if (searchedInMemory.isPresent())
            return new InorganicResult(searchedInMemory.get());

        Optional<ClassifierResult> classifierResult = classifierService.classify(input);

        if (classifierResult.isPresent() && classifierService.isOrganic(classifierResult.get()))
            return new InorganicResult(classifierResultToMenuSuggestion.get(classifierResult.get()));

        return smartSearch(input);
    }

    InorganicResult smartSearch(String input) {
        // TODO correctiosn & similarity
        return enrichedSearch(input);
    }

    InorganicResult enrichedSearch(String input) {
        if (!webSearchComponent.search(input)) {
            logger.warn("Couldn't find inorganic \"" + input + "\".");
            return InorganicResult.notFound();
        }

        // Check if it was already in the DB:

        String[] words = webSearchComponent.getTitle().trim().split("\\s+");
        String firstWord = words[0];

        if (firstWord.equals("ácido"))
            firstWord += words[1];

        Optional<InorganicModel> searchedInMemory = fetch(firstWord);
        if (searchedInMemory.isPresent()) {
            logger.warn("Searched inorganic \"" + input + "\" was: " + searchedInMemory.get());
            return new InorganicResult(searchedInMemory.get()); // TODO with suggestion
        }

        // Parse the inorganic:

        Optional<InorganicModel> parsedInorganic = tryParseWeb(webSearchComponent.getAddress());

        if (parsedInorganic.isEmpty())
            return InorganicResult.notFound();

        // Check again if it was already in the DB:

        searchedInMemory = fetch(parsedInorganic.get().getFormula());
        if (searchedInMemory.isPresent()) {
            logger.warn("Parsed inorganic \"" + input + "\" was: " + searchedInMemory.get());
            return new InorganicResult(searchedInMemory.get()); // TODO with suggestion
        }

        return processNewlyLearned(parsedInorganic.get()); // TODO with suggestion
    }

    String complete(String input) {
        return completionComponent.tryComplete(input);
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

        metricsService.inorganicAutocompleted();
        metricsService.inorganicSearched(inorganicResult.isFound());

        return inorganicResult;
    }

    // Private:

    private Optional<InorganicModel> fetch(String input) {
        String normalizedInput = Normalizer.get(input);
        Optional<Integer> id = cacheComponent.find(normalizedInput);

        if (id.isEmpty())
            return Optional.empty();

        Optional<InorganicModel> inorganicModel = inorganicRepository.findById(id.get());

        if (inorganicModel.isEmpty()) {
            logger.warn("Discrepancy between DB and cached ID: " + id);
            return Optional.empty();
        }

        return inorganicModel;
    }

    private Optional<InorganicModel> tryParseWeb(String url) {
        try {
            InorganicModel parsedInorganic = webParseComponent.parse(url);
            return Optional.of(parsedInorganic);
        } catch (IllegalArgumentException illegalArgumentException) {
            logger.warn("Exception parsing FQPage: " + url + ". " + illegalArgumentException.getMessage());
            return Optional.empty();
        } catch (Exception exception) {
            errorService.log("Exception parsing FQPage: " + url, exception.toString(), this.getClass());
            return Optional.empty();
        }
    }

    private InorganicResult processNewlyLearned(InorganicModel learnedInorganic) {
        Optional<Float> newMolecularMass = molecularMassService.get(learnedInorganic.getFormula());

        if (newMolecularMass.isPresent()) {
            String molecularMass = String.format("%.2f", newMolecularMass.get()).replace(",", ".");
            learnedInorganic.setMolecularMass(molecularMass);
        }

        inorganicRepository.save(learnedInorganic);
        cacheComponent.add(learnedInorganic);

        metricsService.inorganicLearned();
        logger.info("Learned inorganic: " + learnedInorganic);

        return new InorganicResult(learnedInorganic);
    }

}
