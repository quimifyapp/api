package com.quimify.api.inorganic;

import com.quimify.api.error.ErrorService;
import com.quimify.api.utils.Normalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// This class solves inorganic completions.

@Component
@EnableScheduling
class AutocompleteComponent { // TODO rename "CompleteComponent"

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    InorganicRepository inorganicRepository; // DB connection

    @Autowired
    ErrorService errorService; // API errors logic

    private final Map<String, Integer> normalizedTextToId = new LinkedHashMap<>(); // Cache

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(); // Impedes reading cache while writing
    private Map<String, Integer> shadowNormalizedTextToId; // Secondary cache to read when main one is being written

    // Administration:

    @Scheduled(fixedDelay = 5 * 60 * 1000) // At startup, then once every 5 minutes
    private void tryUpdateCache() {
        try {
            shadowNormalizedTextToId = new LinkedHashMap<>(normalizedTextToId);
            readWriteLock.writeLock().lock();
            updateCache();
        } catch (Exception exception) {
            errorService.log("Exception updating cache", exception.toString(), this.getClass());
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private void updateCache() {
        normalizedTextToId.clear();

        for (InorganicModel inorganicModel : inorganicRepository.findAllByOrderBySearchCountDesc())
            putNormalized(inorganicModel);

        logger.info("Inorganic completion cache updated.");
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

    // Internal:

    protected String tryAutoComplete(String input) { // TODO rename "tryComplete"
        if (!readWriteLock.readLock().tryLock())
            return tryAutoCompleteUsing(input, shadowNormalizedTextToId); // Main one is being written

        try {
            return tryAutoCompleteUsing(input, normalizedTextToId);
        } finally {
            readWriteLock.readLock().unlock(); // Only executed if the lock was acquired
        }
    }

    protected String tryAutoCompleteUsing(String input, Map<String, Integer> cache) { // TODO rename "tryComplete"
        try {
            String normalizedInput = Normalizer.get(input);

            for (Map.Entry<String, Integer> entry : cache.entrySet())
                if (entry.getKey().startsWith(normalizedInput))
                    return findNormalizedTextIn(entry.getKey(), entry.getValue());
        } catch (Exception exception) {
            errorService.log("Exception completing: " + input, exception.toString(), this.getClass());
        }

        return "";
    }

    private String findNormalizedTextIn(String normalizedText, Integer id) {
        Optional<InorganicModel> inorganicModel = inorganicRepository.findById(id);

        if (inorganicModel.isEmpty()) {
            errorService.log("Discrepancy between IDs map and DB", id.toString(), this.getClass());
            return "";
        }

        List<String> names = Arrays.asList(
                inorganicModel.get().getStockName(),
                inorganicModel.get().getSystematicName(),
                inorganicModel.get().getTraditionalName(),
                inorganicModel.get().getCommonName()
        );

        for (String name : names)
            if (normalizedText.equals(Normalizer.get(name))) // Null safe
                return name;

        // Here, 'normalizedText' either comes from formula or a search tag
        return inorganicModel.get().getFormula();
    }

}
