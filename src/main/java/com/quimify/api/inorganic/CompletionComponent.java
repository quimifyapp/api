package com.quimify.api.inorganic;

import com.quimify.api.error.ErrorService;
import com.quimify.api.utils.Normalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@EnableScheduling
public abstract class CompletionComponent<InorganicModel extends com.quimify.api.inorganic.InorganicModel> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected abstract InorganicRepository<InorganicModel> getRepository();

    protected abstract CacheComponent<InorganicModel> getCacheComponent();

    @Autowired
    ErrorService errorService; // API errors logic

    // Constants:

    public static final String notFound = "";

    // Internal:

    String tryComplete(String input) {
        try {
            String normalizedInput = Normalizer.get(input);
            Optional<Integer> id = getCacheComponent().findCompletion(normalizedInput);

            if (id.isPresent())
                return findNormalizedTextIn(normalizedInput, id.get());
        } catch (Exception exception) {
            errorService.log("Exception completing: " + input, exception.toString(), getClass());
        }

        return notFound;
    }

    // Private:

    private String findNormalizedTextIn(String normalizedInput, Integer id) {
        Optional<InorganicModel> inorganicModel = getRepository().findById(id);

        if (inorganicModel.isEmpty()) {
            logger.warn("Discrepancy between DB and cached ID: {}", id);
            return notFound;
        }

        return findNormalizedTextIn(normalizedInput, inorganicModel.get());
    }

    private String findNormalizedTextIn(String normalizedInput, InorganicModel inorganicModel) {
        List<String> names = Arrays.asList(
                inorganicModel.getStockName(),
                inorganicModel.getSystematicName(),
                inorganicModel.getTraditionalName(),
                inorganicModel.getCommonName()
        );

        for (String name : names)
            if (name != null && Normalizer.get(name).startsWith(normalizedInput))
                return name;

        // Here, 'normalizedInput' either comes from formula or a search tag
        return inorganicModel.getFormula();
    }

}
