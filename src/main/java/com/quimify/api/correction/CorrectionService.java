package com.quimify.api.correction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CorrectionService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    CorrectionRepository correctionRepository;

    // Internal:

    public String correct(String input) {
        for(CorrectionModel correctionModel : correctionRepository.findAllByOrderByPriority()) {
            if (input.matches(correctionModel.getRegexPattern())) {
                String correctedInput = input.replace(correctionModel.getMistake(), correctionModel.getCorrection());

                logger.warn("Corrected \"" + input + "\" to: \"" + correctedInput + "\".");

                input = correctedInput;
            }
        }

        return input;
    }

}
