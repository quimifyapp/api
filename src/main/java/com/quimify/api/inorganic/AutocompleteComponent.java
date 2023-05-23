package com.quimify.api.inorganic;

import com.quimify.api.error.ErrorService;
import com.quimify.api.utils.Normalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
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
    CacheManager cacheManager;

    @Autowired
    InorganicRepository inorganicRepository; // DB connection

    @Autowired
    ErrorService errorService; // API errors logic

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(); // Pauses read and write requests

    // Administration:

    @Scheduled(fixedDelay = 5 * 1000) // At startup, then once every 5 seconds // TODO fix time
    private void tryUpdateCache() {
        try {
            readWriteLock.writeLock().lock();
            updateCache();
        } catch (Exception exception) {
            errorService.log("Exception updating cache", exception.toString(), this.getClass());
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private void updateCache() {
        cacheManager.getCache("cache").clear();

        List<InorganicModel> inorganicModels = inorganicRepository.findAllByOrderBySearchCountDesc();

        for (InorganicModel inorganicModel : inorganicModels)
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
            cacheManager.getCache("cache").put(normalizedText, id);
    }

    private void putNormalized(String text, Integer id) {
        String normalizedText = Normalizer.get(text);

        if (normalizedText != null)
            cacheManager.getCache("cache").put(normalizedText, id);
    }

    // Internal:

    protected String tryAutoComplete(String input) { // TODO rename "tryComplete"
        readWriteLock.readLock().lock();

        try {
            return autoCompleteUsing(input);
        } catch (Exception exception) {
            errorService.log("Exception completing: " + input, exception.toString(), this.getClass());
            return "";
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    private String autoCompleteUsing(String input) { // TODO rename "complete"
        String normalizedInput = Normalizer.get(input);

        for (Map.Entry<Object, Object> entry : getCacheEntrySet()) {
            String normalizedText = (String) entry.getKey();
            Integer id = (Integer) entry.getValue();

            if (normalizedText.startsWith(normalizedInput))
                return findNormalizedTextIn(normalizedText, id);
        }

        return "";
    }

    private Set<Map.Entry<Object, Object>> getCacheEntrySet() {
        CaffeineCache caffeineCache = (CaffeineCache) cacheManager.getCache("cache");
        return caffeineCache.getNativeCache().asMap().entrySet();
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
