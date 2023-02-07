package com.quimify.api.inorganic;

import com.quimify.api.error.ErrorService;
import com.quimify.api.utils.Normalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

// This class solves inorganic autocompletions.

@Component
@EnableScheduling
@Scope("singleton") // Only one instance of this bean will be created and shared between all the components.
class AutocompleteComponent {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    InorganicRepository inorganicRepository; // DB connection

    @Autowired
    ErrorService errorService; // API errors logic

    private List<String> orderedNormalizedTexts = Collections.synchronizedList(new ArrayList<>());
    private Map<String, Integer> normalizedTextToId = Collections.synchronizedMap(new LinkedHashMap<>());

    // Administration:

    @Scheduled(fixedDelay = 24 * 60 * 60 * 1000) // At startup, then once every 24h hours
    private void updateCacheDaily() {
        updateCache();
    }

    // Internal:

    protected String autoComplete(String input) {
        String normalizedInput = Normalizer.get(input);

        for (String normalizedText : orderedNormalizedTexts)
            if (normalizedText.startsWith(normalizedInput))
                return findNormalizedTextIn(normalizedText);

        return "";
    }

    protected void saveInCache(InorganicModel inorganicModel) {
        putIn(inorganicModel, orderedNormalizedTexts, normalizedTextToId);
    }

    // Private:

    private void updateCache() {
        List<String> newCache = Collections.synchronizedList(new ArrayList<>());
        Map<String, Integer> newKeyToId = Collections.synchronizedMap(new LinkedHashMap<>());

        for (InorganicModel inorganicModel : inorganicRepository.findAllByOrderBySearchCountDesc())
            putIn(inorganicModel, newCache, newKeyToId);

        orderedNormalizedTexts = newCache;
        normalizedTextToId = newKeyToId;

        logger.info("Inorganic autocompletion cache updated.");
    }

    private void putIn(InorganicModel inorganicModel, List<String> cache, Map<String, Integer> keyToId) {
        addSearchTagIn(inorganicModel.getFormula(), inorganicModel.getId(), cache, keyToId);

        addSearchTagIn(inorganicModel.getStockName(), inorganicModel.getId(), cache, keyToId);
        addSearchTagIn(inorganicModel.getSystematicName(), inorganicModel.getId(), cache, keyToId);
        addSearchTagIn(inorganicModel.getTraditionalName(), inorganicModel.getId(), cache, keyToId);
        addSearchTagIn(inorganicModel.getCommonName(), inorganicModel.getId(), cache, keyToId);

        for (String normalizedText : inorganicModel.getSearchTags())
            keyToId.put(normalizedText, inorganicModel.getId());
    }

    private void addSearchTagIn(String text, Integer id, List<String> cache, Map<String, Integer> keyToId) {
        String normalizedText = Normalizer.get(text);

        if (normalizedText != null) {
            cache.add(normalizedText);
            keyToId.put(normalizedText, id);
        }
    }

    private String findNormalizedTextIn(String normalizedText) {
        Integer id = normalizedTextToId.get(normalizedText);

        if (id == null) {
            errorService.log("Discrepancy between texts list and IDs map", normalizedText, this.getClass());
            return "";
        }

        Optional<InorganicModel> inorganicModel = inorganicRepository.findById(id);

        if (inorganicModel.isEmpty()) {
            errorService.log("Discrepancy between IDs map and DB", id.toString(), this.getClass());
            return "";
        }

        String completion = inorganicModel.get().getStockName();
        if (normalizedText.equals(Normalizer.get(completion))) // Null safe
            return completion;

        completion = inorganicModel.get().getSystematicName();
        if (normalizedText.equals(Normalizer.get(completion)))
            return completion;

        completion = inorganicModel.get().getTraditionalName();
        if (normalizedText.equals(Normalizer.get(completion)))
            return completion;

        completion = inorganicModel.get().getCommonName();
        if (normalizedText.equals(Normalizer.get(completion)))
            return completion;

        return inorganicModel.get().getFormula(); // 'normalizedText' comes from formula or a search tag
    }

}
