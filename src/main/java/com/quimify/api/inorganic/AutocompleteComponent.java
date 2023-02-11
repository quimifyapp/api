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

    private List<String> normalizedTexts = Collections.synchronizedList(new ArrayList<>());
    private Map<String, Integer> normalizedTextToId = Collections.synchronizedMap(new LinkedHashMap<>());

    // Administration:

    @Scheduled(fixedDelay = 30 * 60 * 1000) // At startup, then once every 30 minutes
    private void updateCacheDaily() {
        updateCache();
    }

    // Internal:

    protected String autoComplete(String input) {
        String normalizedInput = Normalizer.get(input);

        for (String normalizedText : normalizedTexts)
            if (normalizedText.startsWith(normalizedInput))
                return findNormalizedTextIn(normalizedText);

        return "";
    }

    protected void saveInCache(InorganicModel inorganicModel) {
        putIn(inorganicModel, normalizedTexts, normalizedTextToId);
    }

    // Private:

    private void updateCache() {
        List<String> newNormalizedTexts = Collections.synchronizedList(new ArrayList<>());
        Map<String, Integer> newKeyToId = Collections.synchronizedMap(new LinkedHashMap<>());

        for (InorganicModel inorganicModel : inorganicRepository.findAllByOrderBySearchCountDesc())
            putIn(inorganicModel, newNormalizedTexts, newKeyToId);

        normalizedTexts = newNormalizedTexts;
        normalizedTextToId = newKeyToId;

        logger.info("Inorganic autocompletion cache updated.");
    }

    private void putIn(InorganicModel inorganicModel, List<String> normalizedTexts, Map<String, Integer> keyToId) {
        addSearchTagIn(inorganicModel.getFormula(), inorganicModel.getId(), normalizedTexts, keyToId);

        addSearchTagIn(inorganicModel.getStockName(), inorganicModel.getId(), normalizedTexts, keyToId);
        addSearchTagIn(inorganicModel.getSystematicName(), inorganicModel.getId(), normalizedTexts, keyToId);
        addSearchTagIn(inorganicModel.getTraditionalName(), inorganicModel.getId(), normalizedTexts, keyToId);
        addSearchTagIn(inorganicModel.getCommonName(), inorganicModel.getId(), normalizedTexts, keyToId);

        for (String normalizedText : inorganicModel.getSearchTags()) {
            normalizedTexts.add(normalizedText);
            keyToId.put(normalizedText, inorganicModel.getId());
        }
    }

    private void addSearchTagIn(String text, Integer id, List<String> normalizedTexts, Map<String, Integer> keyToId) {
        String normalizedText = Normalizer.get(text);

        if (normalizedText != null) {
            normalizedTexts.add(normalizedText);
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
