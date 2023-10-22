package com.quimify.api.classification;

import com.quimify.api.error.ErrorService;
import com.quimify.api.settings.SettingsService;
import com.quimify.api.utils.Connection;
import com.quimify.api.utils.Normalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

// This class classifies input and handles calls to the Quimify Classifier AI API when necessary.

@Service
public class ClassificationService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ClassificationRepository classificationRepository; // DB connection

    @Autowired
    SettingsService settingsService;

    // TODO metrics

    @Autowired
    ErrorService errorService;

    // Constants:

    private static final int notFoundResultCode = -1;

    // Public:

    public Optional<Classification> classify(String input) {
        String adaptedInput = adaptInput(input);

        for (ClassificationModel classificationModel : classificationRepository.findAllByOrderByPriority())
            if (adaptedInput.matches(classificationModel.getRegexPattern())) {
                logger.warn("Classified \"" + input + "\" with DB: " + classificationModel.getClassification() + ".");
                return filteredResult(input, classificationModel.getClassification());
            }

        return classifyWithAi(input);
    }

    public boolean isInorganic(Classification classification) {
        return classification == Classification.inorganicFormula || classification == Classification.inorganicName;
    }

    // Private:

    private String adaptInput(String input) {
        input = input.replace("⁺", "+"); // TODO remove (client won't send ⁺ anymore)
        input = Normalizer.getWithSpacesAndSymbols(input);

        return input;
    }

    private Optional<Classification> classifyWithAi(String input) {
        try {
            String response = new Connection(settingsService.getClassifierAiUrl(), input).getText();
            int resultCode = Integer.parseInt(response);

            if (resultCode != notFoundResultCode) {
                Classification classification = Classification.values()[resultCode];
                logger.warn("Classified \"" + input + "\" with AI: " + classification + ".");
                return filteredResult(input, classification);
            }
            else errorService.log("Classifier AI returned " + notFoundResultCode, input, getClass());
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
                if (input.length() > 14)
                    result = Optional.empty();
                break;
            case organicFormula:
                if (input.length() < 3 || input.length() > 100)
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
                if (input.length() < 15 || input.length() > 125)
                    result = Optional.of(Classification.chemicalProblem); // It's part of a greater chemical problem
                break;
            case chemicalReaction:
                if (input.length() < 3)
                    result = Optional.empty(); // It's something like "+a"
                break;
        }

        if(result.isEmpty() || !result.get().equals(classification))
            logger.warn("Classification [" + classification + "] was filtered to: " + result + ".");

        return result;
    }

}
