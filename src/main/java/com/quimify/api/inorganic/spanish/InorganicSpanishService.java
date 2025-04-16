package com.quimify.api.inorganic.spanish;

import com.quimify.api.classification.ClassificationService;
import com.quimify.api.correction.CorrectionService;
import com.quimify.api.inorganic.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InorganicSpanishService extends InorganicService<InorganicSpanishModel> {

    @Autowired
    InorganicSpanishRepository repository;

    @Autowired
    CacheSpanishComponent cacheComponent;

    @Autowired
    ClassificationService classificationService; // TODO class ClassificationEnglishService

    @Autowired
    CorrectionService correctionService; // TODO class CorrectionEnglishService

    @Autowired
    CompletionSpanishComponent completionComponent;

    @Autowired
    WebParseSpanishComponent webParseComponent;

    @Override
    protected InorganicSpanishRepository getRepository() {
        return repository;
    }

    @Override
    protected CacheSpanishComponent getCacheComponent() {
        return cacheComponent;
    }

    @Override
    protected ClassificationService getClassificationService() {
        return classificationService;
    }

    @Override
    protected CorrectionService getCorrectionService() {
        return correctionService;
    }

    @Override
    protected CompletionSpanishComponent getCompletionComponent() {
        return completionComponent;
    }

    @Override
    protected WebParseSpanishComponent getWebParseComponent() {
        return webParseComponent;
    }
}
