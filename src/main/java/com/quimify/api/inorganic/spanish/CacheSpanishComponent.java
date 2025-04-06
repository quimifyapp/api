package com.quimify.api.inorganic.spanish;

import com.quimify.api.inorganic.CacheComponent;
import com.quimify.api.inorganic.InorganicRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class CacheSpanishComponent extends CacheComponent<InorganicSpanishModel> {

    @Autowired
    InorganicSpanishRepository repository;

    @Override
    protected InorganicRepository<InorganicSpanishModel> getRepository() {
        return repository;
    }
}
