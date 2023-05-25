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

// This class holds a cache of all inorganics in the DB.

@Component
@EnableScheduling
class CacheComponent {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    InorganicRepository inorganicRepository; // DB connection

    @Autowired
    ErrorService errorService; // API errors logic

    private final Map<String, Integer> normalizedTextToId = new LinkedHashMap<>(); // Main cache

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(); // Avoids reading main cache while writing
    private Map<String, Integer> shadowNormalizedTextToId; // Secondary cache to be read when main one is being written

    // Internal:

    Optional<Integer> find(String normalizedInput) {
        if (!readWriteLock.readLock().tryLock())
            return findIn(normalizedInput, shadowNormalizedTextToId); // Main one is being written

        try {
            return findIn(normalizedInput, normalizedTextToId);
        } finally {
            readWriteLock.readLock().unlock(); // Only executed if the lock was acquired
        }
    }

    Optional<Integer> findStartingWith(String normalizedInput) {
        if (!readWriteLock.readLock().tryLock())
            return findStartingWithIn(normalizedInput, shadowNormalizedTextToId); // Main one is being written

        try {
            return findStartingWithIn(normalizedInput, normalizedTextToId);
        } finally {
            readWriteLock.readLock().unlock(); // Only executed if the lock was acquired
        }
    }

    @Scheduled(fixedDelay = 5 * 60 * 1000) // At startup, then once every 5 minutes
    void tryUpdateCache() {
        try {
            shadowNormalizedTextToId = new LinkedHashMap<>(normalizedTextToId);
            readWriteLock.writeLock().lock();
            updateCache();
        } catch (Exception exception) {
            errorService.log("Exception updating cache", exception.toString(), getClass());
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    // Private:

    private Optional<Integer> findIn(String normalizedInput, Map<String, Integer> cache) {
        return Optional.ofNullable(cache.get(normalizedInput));
    }

    private Optional<Integer> findStartingWithIn(String normalizedInput, Map<String, Integer> cache) {
        for (Map.Entry<String, Integer> entry : cache.entrySet())
            if (entry.getKey().startsWith(normalizedInput))
                return Optional.of(entry.getValue());

        return Optional.empty();
    }

    private void updateCache() {
        normalizedTextToId.clear();

        for (InorganicModel inorganicModel : inorganicRepository.findAllByOrderBySearchCountDesc())
            putNormalized(inorganicModel);

        logger.info("Inorganic cache updated.");
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
        if (text != null)
            normalizedTextToId.put(Normalizer.get(text), id);
    }

}

