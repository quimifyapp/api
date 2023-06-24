package com.quimify.api.classifier;

import com.quimify.api.error.ErrorService;
import com.quimify.api.settings.SettingsService;
import com.quimify.api.utils.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    private static final int notFoundResultCode = -1;

    private static final List<String> chemistryProblemWords = List.of( // TODO test with all DB and random organics
            "el", "en", "ml",
            "con", "del", "las", "los ", "por", "que", "una",
            "cada", "cual", "masa", "para", "moles",
            "tiene", "gramos",
            "litros", "numero",
            "presion", "volumen",
            "cantidad", "contiene", "preparar", "reaccion", "solucion",
            "disolucion", "porcentaje", "siguientes",
            "temperatura",
            "concentracion"
    );

    // TODO reactions vocabulary

    // Public:

    public Optional<ClassifierResult> classify(String input) {
        try {
            if(isChemicalReaction(input))
                return Optional.of(ClassifierResult.chemicalReaction);

            if(isChemistryProblem(input))
                return Optional.of(ClassifierResult.chemistryProblem);

            String response = new Connection(settingsService.getClassifierUrl(), input).getText();
            int result = Integer.parseInt(response);

            if (result == notFoundResultCode)
                return Optional.empty();

            return Optional.of(ClassifierResult.values()[result]);
        } catch (Exception exception) {
            errorService.log("Exception calling classifier for: " + input, exception.toString(), getClass());
            return Optional.empty();
        }
    }

    public boolean isOrganic(ClassifierResult result) {
        return result == ClassifierResult.organicFormula || result == ClassifierResult.organicName;
    }

    // Private:

    private boolean isChemicalReaction(String input) {
        // TODO
        return false;
    }

    private boolean isChemistryProblem(String input) {
        return chemistryProblemWords.stream().anyMatch((input::contains));
    }

}
