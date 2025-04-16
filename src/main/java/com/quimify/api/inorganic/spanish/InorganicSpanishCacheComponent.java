package com.quimify.api.inorganic.spanish;

import com.quimify.api.inorganic.CacheComponent;
import com.quimify.api.inorganic.InorganicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("inorganicSpanishCacheComponent") // Specify a unique bean name
public class InorganicSpanishCacheComponent extends CacheComponent<InorganicSpanishModel> {

    private final InorganicSpanishRepository inorganicSpanishRepository;

    @Autowired
    public InorganicSpanishCacheComponent(InorganicSpanishRepository inorganicSpanishRepository) {
        this.inorganicSpanishRepository = inorganicSpanishRepository;
    }

    @Override
    protected InorganicRepository<InorganicSpanishModel> getRepository() {
        return inorganicSpanishRepository;
    }

}
