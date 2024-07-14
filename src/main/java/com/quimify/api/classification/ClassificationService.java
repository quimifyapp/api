package com.quimify.api.classification;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quimify.api.error.ErrorService;
import com.quimify.api.health.HealthResult;
import com.quimify.api.settings.SettingsService;
import com.quimify.api.utils.Connection;
import com.quimify.api.utils.Normalizer;

// This class classifies input and handles calls to the Quimify Classifier AI API when necessary.

@Service
public class ClassificationService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ClassificationRepository classificationRepository;

    @Autowired
    SettingsService settingsService;

    @Autowired
    ErrorService errorService;

    // Public:

    public Optional<Classification> classify(String input) {
        String adaptedInput = Normalizer.getWithSpacesAndSymbols(input);

        for (ClassificationModel classificationModel : classificationRepository.findAllByOrderByPriority())
            if (adaptedInput.matches(classificationModel.getRegexPattern())) {
                logger.warn("Classified \"{}\" with DB: {}.", input, classificationModel.getClassification());

                return filteredResult(input, classificationModel.getClassification());
            }

        return classifyWithAi(input);
    }

    public boolean isInorganic(Classification classification) {
        return classification == Classification.inorganicFormula || classification == Classification.inorganicName;
    }

    public HealthResult checkHealth() {
        String testFormula = "h2o";

        Optional<Classification> inorganicFormulaResult = classify(testFormula);

        if (inorganicFormulaResult.isEmpty() || inorganicFormulaResult.get() != Classification.inorganicFormula) {
            return new HealthResult(false, "Error classifying inorganic formula: " + testFormula);
        }

        return new HealthResult(true, "Classification health check successful");
    }

    // Private:

    private Optional<Classification> classifyWithAi(String input) {
        try {
            String name = new Connection(settingsService.getClassifierAiUrl(), input).getText();

            if (name.isEmpty()) {
                errorService.log("Classifier AI returned an empty response", input, getClass());
                return Optional.empty();
            }

            Classification classification = Classification.valueOf(name);

            logger.warn("Classified \"{}\" with AI: {}.", input, classification);

            return filteredResult(input, classification);
        } catch (Exception exception) {
            errorService.log("Exception calling Classifier AI for: " + input, exception.toString(), getClass());
        }

        return Optional.empty();
    }

    private Optional<Classification> filteredResult(String input, Classification classification) {
        if (classification == null)
            return Optional.empty();

        Optional<Classification> result = Optional.of(classification);

        // Boundaries are arbitrary and estimated through simple observation:

        switch (classification) {
            case inorganicFormula:
                if (input.length() > 16)
                    result = Optional.empty();
                break;
            case organicFormula:
                if (input.length() < 2 || input.length() > 100)
                    result = Optional.empty();
                else if (!(input.contains("C") || input.contains("c")))
                    result = Optional.empty();
                break;
            case inorganicName:
                if (input.length() < 4 || input.length() > 50)
                    result = Optional.empty();
                break;
            case organicName:
                if (input.length() < 4 || input.length() > 125)
                    result = Optional.empty();
                break;
            case molecularMassProblem:
                if (input.length() > 125)
                    result = Optional.of(Classification.chemicalProblem); // It's part of a greater chemical problem
                break;
            case chemicalReaction:
                if (input.length() < 3)
                    result = Optional.empty(); // It's something like "+a"
                break;
        }

        if (result.isEmpty() || !result.get().equals(classification))
            logger.warn("Classification of \"{}\" was filtered: {} -> {}.", input, classification, result);

        return result;
    }

}
