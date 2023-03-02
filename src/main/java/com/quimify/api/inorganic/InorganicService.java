package com.quimify.api.inorganic;

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
    AutocompleteComponent autocompleteComponent; // Autocompletions logic

    @Autowired
    WebSearchComponent webSearchComponent; // Web searches logic

    @Autowired
    FQPageComponent fqPageComponent; // Inorganic web pages logic

    @Autowired
    MolecularMassService molecularMassService; // Molecular masses logic

    @Autowired
    SettingsService settingsService; // Settings logic

    @Autowired
    NotFoundQueryService notFoundQueryService; // Not found queries logic

    @Autowired
    MetricsService metricsService; // Daily metrics logic

    @Autowired
    ErrorService errorService; // API errors logic

    // Client:

    protected String autoComplete(String input) {
        return autocompleteComponent.autoComplete(input);
    }

    protected InorganicResult searchFromCompletion(String completion) {
        InorganicResult inorganicResult;

        Optional<InorganicModel> searchedInorganic = searchInDatabase(completion);
        if (searchedInorganic.isPresent())
            inorganicResult = new InorganicResult(searchedInorganic.get());
        else {
            errorService.log("Completion not in DB", completion, this.getClass());
            inorganicResult = InorganicResult.notFound;
        }

        metricsService.inorganicAutocompleted();
        metricsService.inorganicSearched(inorganicResult.isPresent());

        return inorganicResult;
    }

    protected InorganicResult search(String input) {
        InorganicResult inorganicResult;

        Optional<InorganicModel> searchedInMemory = searchInDatabase(input);

        inorganicResult = searchedInMemory.map(InorganicResult::new).orElseGet(() -> searchOnTheWeb(input));

        if(!inorganicResult.isPresent())
            notFoundQueryService.log(input, this.getClass());

        metricsService.inorganicSearched(inorganicResult.isPresent());

        return inorganicResult;
    }

    // Private:

    private InorganicResult searchOnTheWeb(String input) {
        if(!webSearchComponent.search(input)) {
            logger.warn("Couldn't find inorganic \"" + input + "\".");
            return InorganicResult.notFound;
        }

        // Check if it was already in the DB:

        String[] words = webSearchComponent.getTitle().trim().split(" ");
        String firstWord = words[0];

        if (firstWord.equals("ácido"))
            firstWord += words[1];

        Optional<InorganicModel> searchedInDatabase = searchInDatabase(firstWord);
        if (searchedInDatabase.isPresent()) {
            logger.warn("Searched inorganic \"" + input + "\" was: " + searchedInDatabase.get());
            return new InorganicResult(searchedInDatabase.get());
        }

        // Parse the inorganic:

        Optional<InorganicModel> parsedInorganic = tryParseFQ(webSearchComponent.getAddress());

        if (parsedInorganic.isEmpty())
            return InorganicResult.notFound;

        // Check again if it was already in the DB:

        searchedInDatabase = searchInDatabase(parsedInorganic.get().getFormula());
        if (searchedInDatabase.isPresent()) {
            logger.warn("Parsed inorganic \"" + input + "\" was: " + searchedInDatabase.get());
            return new InorganicResult(searchedInDatabase.get());
        }

        return processNewlyLearned(parsedInorganic.get());
    }

    private Optional<InorganicModel> searchInDatabase(String input) {
        String normalizedInput = Normalizer.get(input);

        for (InorganicModel inorganicModel : inorganicRepository.findAllByOrderBySearchCountDesc())
            if(matches(normalizedInput, inorganicModel)) {
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

        for(String searchTag : inorganicModel.getSearchTags())
            if(normalizedInput.equals(searchTag))
                return true;

        return false;
    }

    private Optional<InorganicModel> tryParseFQ(String url) {
        try {
            InorganicModel parsedInorganic = fqPageComponent.parseInorganic(url);
            return Optional.of(parsedInorganic);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            logger.warn("Exception parsing FQPage: " + url + ". " + illegalArgumentException.getMessage());
            return Optional.empty();
        }
        catch (Exception exception) {
            errorService.log("Exception parsing FQPage: " + url, exception.toString(), this.getClass());
            return Optional.empty();
        }
    }

    private InorganicResult processNewlyLearned(InorganicModel learnedInorganic) {
        Optional<Float> newMolecularMass = molecularMassService.get(learnedInorganic.getFormula());

        if(newMolecularMass.isPresent()) {
            String molecularMass = String.format("%.2f", newMolecularMass.get()).replace(",", ".");
            learnedInorganic.setMolecularMass(molecularMass);
        }

        inorganicRepository.save(learnedInorganic);
        autocompleteComponent.saveInCache(learnedInorganic);

        metricsService.inorganicLearned();
        logger.info("Learned inorganic: " + learnedInorganic);

        return new InorganicResult(learnedInorganic);
    }

}
