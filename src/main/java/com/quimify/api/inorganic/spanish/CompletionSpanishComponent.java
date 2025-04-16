package com.quimify.api.inorganic.spanish;

import com.quimify.api.inorganic.CompletionComponent;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

@Component
public class CompletionSpanishComponent extends CompletionComponent<InorganicSpanishModel> {

    @Autowired
    InorganicSpanishRepository repository;

    @Autowired
    CacheSpanishComponent cacheComponent;

    @Override
    protected InorganicSpanishRepository getRepository() {
        return repository;
    }

    @Override
    protected CacheSpanishComponent getCacheComponent() {
        return cacheComponent;
    }
}
