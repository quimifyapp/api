package com.quimify.api.inorganic.english;

import com.quimify.api.inorganic.CompletionComponent;
import org.springframework.beans.factory.annotation.Autowired;

public class CompletionEnglishComponent extends CompletionComponent<InorganicEnglishModel> {

    @Autowired
    InorganicEnglishRepository repository;

    @Autowired
    CacheEnglishComponent cacheComponent;

    @Override
    protected InorganicEnglishRepository getRepository() {
        return repository;
    }

    @Override
    protected CacheEnglishComponent getCacheComponent() {
        return cacheComponent;
    }
}
