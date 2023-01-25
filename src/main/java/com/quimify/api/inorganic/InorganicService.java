package com.quimify.api.inorganic;

import com.quimify.api.Normalized;
import com.quimify.api.error.ErrorService;
import com.quimify.api.molecular_mass.MolecularMassService;
import com.quimify.api.settings.SettingsService;
import com.quimify.api.metrics.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

// This class implements the logic behind HTTP methods in "/inorganic".

@Service
public class InorganicService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    InorganicRepository inorganicRepository; // DB connection

    @Autowired
    GoogleComponent googleComponent; // Google searches logic

    @Autowired
    BingComponent bingComponent; // Bing searches logic

    @Autowired
    FQPageComponent fqPageComponent; // Inorganic web pages logic

    @Autowired
    MolecularMassService molecularMassService; // Molecular masses logic

    @Autowired
    SettingsService settingsService; // Settings logic

    @Autowired
    MetricsService metricsService; // Daily metrics logic

    @Autowired
    ErrorService errorService; // API errors logic

    private static final List<InorganicSearchTagModel> searchTagsCache = new ArrayList<>();

    // Administration:

    public void refreshAutocompletion() {
        List<InorganicSearchTagModel> newSearchTags = inorganicRepository.findAllByOrderBySearchCountDesc().stream()
                .flatMap(inorganicModel -> inorganicModel.getSearchTags().stream())
                .collect(Collectors.toList());

        searchTagsCache.clear();
        searchTagsCache.addAll(newSearchTags);

        logger.info("Inorganic search tags updated in memory.");
    }

    // Client autocompletion:

    protected String autoComplete(String input) { // TODO clean code
        String normalizedInput = Normalized.of(input);

        for (InorganicSearchTagModel searchTag : searchTagsCache)
            if (searchTag.getNormalizedTag().startsWith(normalizedInput)) {
                Optional<InorganicModel> inorganicModel = inorganicRepository.findBySearchTagsContaining(searchTag);

                if (inorganicModel.isEmpty()) {
                    errorService.saveError("Search tag not in DB", searchTag.getNormalizedTag(), this.getClass());
                    return "";
                }

                String completion = inorganicModel.get().getStockName();
                if (completion != null && Normalized.of(completion).startsWith(normalizedInput))
                    return completion;

                completion = inorganicModel.get().getSystematicName();
                if (completion != null && Normalized.of(completion).startsWith(normalizedInput))
                    return completion;

                completion = inorganicModel.get().getTraditionalName();
                if (completion != null && Normalized.of(completion).startsWith(normalizedInput))
                    return completion;

                completion = inorganicModel.get().getOtherName();
                if (completion != null && Normalized.of(completion).startsWith(normalizedInput))
                    return completion;

                return inorganicModel.get().getFormula(); // Formula or a search tag
            }

        return "";
    }

    // Client searching:

    protected InorganicResult searchFromCompletion(String completion) {
        InorganicResult inorganicResult;

        Optional<InorganicModel> searchedInorganic = searchInDatabase(completion);
        if (searchedInorganic.isPresent())
            inorganicResult = new InorganicResult(searchedInorganic.get());
        else {
            errorService.saveError("Completion not in DB", completion, this.getClass());
            inorganicResult = InorganicResult.notFound;
        }

        metricsService.countInorganicAutocompleted();
        metricsService.countSearchedInorganic(inorganicResult.isPresent(), false);

        return inorganicResult;
    }

    protected InorganicResult search(String input, Boolean isPicture) {
        InorganicResult inorganicResult;

        Optional<InorganicModel> searchedInMemory = searchInDatabase(input);

        inorganicResult = searchedInMemory.map(InorganicResult::new).orElseGet(() -> searchOnTheWeb(input));

        metricsService.countSearchedInorganic(inorganicResult.isPresent(), isPicture);

        return inorganicResult;
    }

    // Private methods:

    private InorganicResult searchOnTheWeb(String input) { // TODO break up
        Optional<WebSearchResult> searchResult;

        searchResult = googleComponent.search(input);
        if (searchResult.isEmpty())
            searchResult = bingComponent.freeSearch(input);
        if (searchResult.isEmpty())
            searchResult = bingComponent.paidSearch(input);

        if (searchResult.isEmpty() || !searchResult.get().isFound()) {
            logger.warn("Couldn't find inorganic \"" + input + "\".");
            return InorganicResult.notFound;
        }

        String[] words = searchResult.get().getTitle().trim().split(" ");
        String firstWord = words[0];

        if (firstWord.equals("ácido"))
            firstWord += words[1];

        Optional<InorganicModel> searchedInDatabase = searchInDatabase(firstWord);
        if (searchedInDatabase.isPresent()) {
            logger.warn("Searched inorganic \"" + input + "\" was: " + searchedInDatabase.get());
            return new InorganicResult(searchedInDatabase.get());
        }

        Optional<InorganicModel> parsedInorganic = tryParseFQ(searchResult.get().getAddress());

        if (parsedInorganic.isEmpty())
            return InorganicResult.notFound;

        searchedInDatabase = searchInDatabase(parsedInorganic.get().getFormula());
        if (searchedInDatabase.isPresent()) {
            logger.warn("Parsed inorganic \"" + input + "\" was: " + searchedInDatabase.get());
            return new InorganicResult(searchedInDatabase.get());
        }

        Float molecularMass = molecularMassService.tryMolecularMassOf(parsedInorganic.get().getFormula());
        parsedInorganic.get().setMolecularMass(String.format("%.2f", molecularMass).replace(",", "."));

        InorganicResult inorganicResult = new InorganicResult(parsedInorganic.get());

        inorganicRepository.save(parsedInorganic.get());
        searchTagsCache.addAll(parsedInorganic.get().getSearchTags());

        metricsService.countInorganicLearned();
        logger.info("New inorganic: " + parsedInorganic.get());

        return inorganicResult;
    }

    private Optional<InorganicModel> searchInDatabase(String input) {
        String normalizedInput = Normalized.of(input);

        for (InorganicModel inorganicModel : inorganicRepository.findAllByOrderBySearchCountDesc())
            if (inorganicModel.getSearchTagsAsStrings().contains(normalizedInput)) {
                inorganicModel.countSearch();
                return Optional.of(inorganicModel);
            }

        return Optional.empty();
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
            errorService.saveError("Exception parsing FQPage: " + url, exception.toString(), this.getClass());
            return Optional.empty();
        }
    }

}
