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

    private final Map<String, Integer> normalizedTextToId = new LinkedHashMap<>();

    // Administration:

    @Scheduled(fixedDelay = 30 * 60 * 1000) // At startup, then once every 30 minutes
    private void updateCacheDaily() {
        updateCache();
    }

    // Internal:

    protected String autoComplete(String input) {
        String normalizedInput = Normalizer.get(input);

        for (Map.Entry<String, Integer> entry : normalizedTextToId.entrySet())
            if (entry.getKey().startsWith(normalizedInput))
                return findNormalizedTextIn(entry.getKey(), entry.getValue());

        return "";
    }

    // Private:

    private void updateCache() {
        List<InorganicModel> inorganicModels = inorganicRepository.findAllByOrderBySearchCountDesc();

        normalizedTextToId.clear();

        for (InorganicModel inorganicModel : inorganicModels)
            putNormalized(inorganicModel);

        logger.info("Inorganic autocompletion cache updated.");
    }

    private void putNormalized(InorganicModel inorganicModel) {
        Integer id = inorganicModel.getId();

        putNormalized(inorganicModel.getFormula(), id);

        putNormalized(inorganicModel.getStockName(), id);
        putNormalized(inorganicModel.getSystematicName(), id);
        putNormalized(inorganicModel.getTraditionalName(), id);
        putNormalized(inorganicModel.getCommonName(), id);

        // Search tags (already normalized):

        for (String normalizedText : inorganicModel.getSearchTags())
            normalizedTextToId.put(normalizedText, id);
    }

    private void putNormalized(String text, Integer id) {
        String normalizedText = Normalizer.get(text);

        if (normalizedText != null)
            normalizedTextToId.put(normalizedText, id);
    }

    private String findNormalizedTextIn(String normalizedText, Integer id) {
        Optional<InorganicModel> inorganicModel = inorganicRepository.findById(id);

        if (inorganicModel.isEmpty()) {
            errorService.log("Discrepancy between IDs map and DB", id.toString(), this.getClass());
            return "";
        }

        List<String> texts = new ArrayList<>();

        texts.add(inorganicModel.get().getStockName());
        texts.add(inorganicModel.get().getSystematicName());
        texts.add(inorganicModel.get().getTraditionalName());
        texts.add(inorganicModel.get().getCommonName());

        for (String text : texts)
            if (normalizedText.equals(Normalizer.get(text))) // Null safe
                return text;

        // Here, 'normalizedText' either comes from formula or a search tag
        return inorganicModel.get().getFormula();
    }

}
