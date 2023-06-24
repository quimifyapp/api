package com.quimify.api.correction;

import com.quimify.api.utils.Normalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CorrectionService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    CorrectionRepository correctionRepository; // DB connection

    // Internal:

    public String correct(String input) {
        for(CorrectionModel correctionModel : correctionRepository.findAll()) {
            String mistake = correctionModel.getMistake();
            String correction = correctionModel.getCorrection();

            input = correctIfWrong(input, mistake, correction);
        }

        return input;
    }

    public String correctNormalized(String normalizedInput) {
        for(CorrectionModel correctionModel : correctionRepository.findAll()) {
            String normalizedMistake = Normalizer.get(correctionModel.getMistake());
            String normalizedCorrection = Normalizer.get(correctionModel.getCorrection());

            normalizedInput = correctIfWrong(normalizedInput, normalizedMistake, normalizedCorrection);
        }

        return normalizedInput;
    }

    // Private:

    private String correctIfWrong(String input, String mistake, String correction) {
        if(!input.contains(mistake))
            return input;

        String correctedInput = input.replace(mistake, correction);

        logger.warn("Corrected \"" + input + "\" into \"" + correctedInput + "\".");

        return correctedInput;
    }

}
