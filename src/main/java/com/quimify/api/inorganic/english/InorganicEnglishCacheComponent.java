package com.quimify.api.inorganic.english;

import com.quimify.api.inorganic.CacheComponent;
import com.quimify.api.inorganic.InorganicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("inorganicEnglishCacheComponent") // Specify a unique bean name
public class InorganicEnglishCacheComponent extends CacheComponent<InorganicEnglishModel> {

    private final InorganicEnglishRepository inorganicEnglishRepository;

    @Autowired
    public InorganicEnglishCacheComponent(InorganicEnglishRepository inorganicEnglishRepository) {
        this.inorganicEnglishRepository = inorganicEnglishRepository;
    }

    @Override
    protected InorganicRepository<InorganicEnglishModel> getRepository() {
        return inorganicEnglishRepository;
    }

}
