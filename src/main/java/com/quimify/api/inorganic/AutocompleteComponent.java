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

    private Map<String, Integer> normalizedTextToId = Collections.synchronizedMap(new LinkedHashMap<>());

    // Administration:

    @Scheduled(fixedDelay = 30 * 60 * 1000) // At startup, then once every 30 minutes
    private void updateCacheDaily() {
        updateCache();
    }

    // Internal:

    protected String autoComplete(String input) {
        String normalizedInput = Normalizer.get(input);

        for (String normalizedText : normalizedTextToId.keySet())
            if (normalizedText.startsWith(normalizedInput))
                return findNormalizedTextIn(normalizedText);

        return "";
    }

    protected void saveInCache(InorganicModel inorganicModel) {
        putIn(inorganicModel, normalizedTextToId);
    }

    // Private:

    private void updateCache() {
        Map<String, Integer> normalizedTextToId = Collections.synchronizedMap(new LinkedHashMap<>());

        for (InorganicModel inorganicModel : inorganicRepository.findAllByOrderBySearchCountDesc())
            putIn(inorganicModel, normalizedTextToId);

        this.normalizedTextToId = normalizedTextToId;

        logger.info("Inorganic autocompletion cache updated.");
    }

    private void putIn(InorganicModel inorganicModel, Map<String, Integer> normalizedTextToId) {
        Integer id = inorganicModel.getId();

        putIn(inorganicModel.getFormula(), id, normalizedTextToId);

        putIn(inorganicModel.getStockName(), id, normalizedTextToId);
        putIn(inorganicModel.getSystematicName(), id, normalizedTextToId);
        putIn(inorganicModel.getTraditionalName(), id, normalizedTextToId);
        putIn(inorganicModel.getCommonName(), id, normalizedTextToId);

        for (String normalizedText : inorganicModel.getSearchTags())
            normalizedTextToId.put(normalizedText, id);
    }

    private void putIn(String text, Integer id, Map<String, Integer> normalizedTextToId) {
        String normalizedText = Normalizer.get(text);

        if (normalizedText != null)
            normalizedTextToId.put(normalizedText, id);
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
