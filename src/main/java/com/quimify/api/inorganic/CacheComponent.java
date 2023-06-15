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

    private final Map<String, Integer> cache = new LinkedHashMap<>(); // Ordered by searches

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(); // Avoids reading main cache while writing
    private Map<String, Integer> shadowCache; // Read while main one is being modified

    // Updating:

    @Scheduled(fixedDelay = 5 * 60 * 1000) // It's called at startup too
    private void tryUpdateCache() {
        try {
            shadowCache = new LinkedHashMap<>(cache);
            readWriteLock.writeLock().lock();

            cache.clear();

            Map<String, Integer> searchTagsCache = new LinkedHashMap<>();

            for (InorganicModel inorganicModel : inorganicRepository.findAllByOrderBySearchCountDesc()) {
                add(inorganicModel);

                for (String normalizedText : inorganicModel.getSearchTags())
                    searchTagsCache.put(normalizedText, inorganicModel.getId()); // Already normalized
            }

            cache.putAll(searchTagsCache); // Search tags go at the end

            logger.info("Inorganic cache updated.");
        } catch (Exception exception) {
            errorService.log("Exception updating cache", exception.toString(), getClass());
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    // Internal:

    void add(InorganicModel inorganicModel) {
        Integer id = inorganicModel.getId();

        addNormalized(inorganicModel.getFormula(), id);
        addNormalized(inorganicModel.getStockName(), id);
        addNormalized(inorganicModel.getSystematicName(), id);
        addNormalized(inorganicModel.getTraditionalName(), id);
        addNormalized(inorganicModel.getCommonName(), id);
    }

    Optional<Integer> find(String normalizedInput) {
        if (!readWriteLock.readLock().tryLock())
            return findIn(normalizedInput, shadowCache); // Main one is being written
        try {
            return findIn(normalizedInput, cache);
        } finally {
            readWriteLock.readLock().unlock(); // Only executed if the lock was acquired
        }
    }

    Optional<Integer> findStartingWith(String normalizedInput) {
        if (!readWriteLock.readLock().tryLock())
            return findStartingWithIn(normalizedInput, shadowCache); // Main one is being written

        try {
            return findStartingWithIn(normalizedInput, cache);
        } finally {
            readWriteLock.readLock().unlock(); // Only executed if the lock was acquired
        }
    }

    // Private:

    private void addNormalized(String text, Integer id) {
        if (text != null)
            cache.put(Normalizer.get(text), id);
    }

    private Optional<Integer> findIn(String normalizedInput, Map<String, Integer> cache) {
        return Optional.ofNullable(cache.get(normalizedInput));
    }

    private Optional<Integer> findStartingWithIn(String normalizedInput, Map<String, Integer> cache) {
        for (Map.Entry<String, Integer> entry : cache.entrySet())
            if (entry.getKey().startsWith(normalizedInput))
                return Optional.of(entry.getValue());

        return Optional.empty();
    }

}

