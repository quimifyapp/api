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
class InorganicService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    InorganicRepository inorganicRepository; // DB connection

    @Autowired
    AutocompleteComponent autocompleteComponent;

    @Autowired
    ClassifierService classifierService;

    @Autowired
    WebSearchComponent webSearchComponent;

    @Autowired
    FqPageComponent fqPageComponent;

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

    private static final Map<ClassifierResult, String> classifierResultToMenuSuggestion = Map.of(
            ClassifierResult.organicFormula, "organic-naming",
            ClassifierResult.organicName, "organic-finding-formula"
    );

    // Client:

    String complete(String input) {
        return autocompleteComponent.tryAutoComplete(input);
    }

    InorganicResult searchFromCompletion(String completion) {
        InorganicResult inorganicResult;

        Optional<InorganicModel> searchedInorganic = databaseSearch(completion);
        if (searchedInorganic.isPresent())
            inorganicResult = new InorganicResult(searchedInorganic.get());
        else {
            errorService.log("Completion not in DB", completion, getClass());
            inorganicResult = InorganicResult.notFound();
        }

        metricsService.inorganicAutocompleted();
        metricsService.inorganicSearched(inorganicResult.isFound());

        return inorganicResult;
    }

    //if(!inorganicResult.isPresent()) TODO
    //        notFoundQueryService.log(input, getClass());

    //metricsService.inorganicSearched(inorganicResult.isPresent());

    InorganicResult search(String input) {
        Optional<InorganicModel> searchedInDatabase = databaseSearch(input);

        if (searchedInDatabase.isPresent())
            return new InorganicResult(searchedInDatabase.get());

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
        if (!webSearchComponent.search(input))
            return InorganicResult.notFound();

        // Check if it was already in the DB:

        String[] words = webSearchComponent.getTitle().trim().split("\\s+");
        String firstWord = words[0];

        if (firstWord.equals("ácido"))
            firstWord += words[1];

        Optional<InorganicModel> searchedInDatabase = databaseSearch(firstWord);
        if (searchedInDatabase.isPresent()) {
            logger.warn("Searched inorganic \"" + input + "\" was: " + searchedInDatabase.get());
            return new InorganicResult(searchedInDatabase.get()); // TODO with suggestion
        }

        // Parse the inorganic:

        Optional<InorganicModel> parsedInorganic = tryParseFqPage(webSearchComponent.getAddress());

        if (parsedInorganic.isEmpty())
            return InorganicResult.notFound();

        // Check again if it was already in the DB:

        searchedInDatabase = databaseSearch(parsedInorganic.get().getFormula());
        if (searchedInDatabase.isPresent()) {
            logger.warn("Parsed inorganic \"" + input + "\" was: " + searchedInDatabase.get());
            return new InorganicResult(searchedInDatabase.get()); // TODO with suggestion
        }

        return processNewlyLearned(parsedInorganic.get()); // TODO with suggestion
    }

    // Private:

    private Optional<InorganicModel> databaseSearch(String input) { // TODO cache?
        String normalizedInput = Normalizer.get(input);

        for (InorganicModel inorganicModel : inorganicRepository.findAll())
            if (matches(normalizedInput, inorganicModel)) {
                inorganicModel.countSearch();
                return Optional.of(inorganicModel);
            }

        return Optional.empty();
    }

    private boolean matches(String normalizedInput, InorganicModel inorganicModel) {
        String possibleMatch = inorganicModel.getFormula();
        if (normalizedInput.equals(Normalizer.get(possibleMatch))) // Null safe
            return true;

        possibleMatch = inorganicModel.getStockName();
        if (normalizedInput.equals(Normalizer.get(possibleMatch)))
            return true;

        possibleMatch = inorganicModel.getSystematicName();
        if (normalizedInput.equals(Normalizer.get(possibleMatch)))
            return true;

        possibleMatch = inorganicModel.getTraditionalName();
        if (normalizedInput.equals(Normalizer.get(possibleMatch)))
            return true;

        possibleMatch = inorganicModel.getCommonName();
        if (normalizedInput.equals(Normalizer.get(possibleMatch)))
            return true;

        for (String searchTag : inorganicModel.getSearchTags())
            if (normalizedInput.equals(searchTag))
                return true;

        return false;
    }

    private Optional<InorganicModel> tryParseFqPage(String url) {
        try {
            InorganicModel parsedInorganic = fqPageComponent.parseInorganic(url);
            return Optional.of(parsedInorganic);
        } catch (IllegalArgumentException illegalArgumentException) {
            logger.warn("Exception parsing FqPage: " + url + ". " + illegalArgumentException.getMessage());
            return Optional.empty();
        } catch (Exception exception) {
            errorService.log("Exception parsing FqPage: " + url, exception.toString(), getClass());
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

        metricsService.inorganicLearned();
        logger.info("Learned inorganic: " + learnedInorganic);

        return new InorganicResult(learnedInorganic);
    }

}
