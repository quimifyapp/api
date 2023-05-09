package com.quimify.api.classifier;

import com.quimify.api.error.ErrorService;
import com.quimify.api.settings.SettingsService;
import com.quimify.api.utils.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

// This class handles calls to the Quimify Classifier API.

@Service
public class ClassifierService {

    @Autowired
    SettingsService settingsService;

    // TODO metrics

    @Autowired
    ErrorService errorService;

    // Constants:

    static final int notFoundResultCode = -1;

    // Public:

    public Optional<ClassifierResult> classify(String text) {
        try {
            String response = new Connection(settingsService.getClassifierUrl(), text).getText();
            int result = Integer.parseInt(response);

            if (result == notFoundResultCode) return Optional.empty();

            return Optional.of(ClassifierResult.values()[result]);
        } catch (Exception exception) {
            errorService.log("Exception calling classifier for: " + text, exception.toString(), getClass());
            return Optional.empty();
        }
    }

    public boolean isInorganic(ClassifierResult result) {
        return result == ClassifierResult.inorganicFormula || result == ClassifierResult.inorganicName;
    }

    public boolean isFormula(ClassifierResult result) {
        return result == ClassifierResult.inorganicFormula || result == ClassifierResult.organicFormula;
    }

}
