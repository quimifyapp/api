package com.quimify.api.inorganic.english;

import com.quimify.api.classification.ClassificationService;
import com.quimify.api.correction.CorrectionService;
import com.quimify.api.inorganic.InorganicService;
import org.springframework.beans.factory.annotation.Autowired;

public class InorganicEnglishService extends InorganicService<InorganicEnglishModel> {

    @Autowired
    InorganicEnglishRepository repository;

    @Autowired
    CacheEnglishComponent cacheComponent;

    @Autowired
    ClassificationService classificationService; // TODO class ClassificationEnglishService

    @Autowired
    CorrectionService correctionService; // TODO class CorrectionEnglishService

    @Autowired
    CompletionEnglishComponent completionComponent;

    @Autowired
    WebParseEnglishComponent webParseComponent;

    @Override
    protected InorganicEnglishRepository getRepository() {
        return repository;
    }

    @Override
    protected CacheEnglishComponent getCacheComponent() {
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
    protected CompletionEnglishComponent getCompletionComponent() {
        return completionComponent;
    }

    @Override
    protected WebParseEnglishComponent getWebParseComponent() {
        return webParseComponent;
    }
}
