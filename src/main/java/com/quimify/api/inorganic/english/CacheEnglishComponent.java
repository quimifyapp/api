package com.quimify.api.inorganic.english;

import com.quimify.api.inorganic.CacheComponent;
import com.quimify.api.inorganic.InorganicRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class CacheEnglishComponent extends CacheComponent<InorganicEnglishModel> {

    @Autowired
    InorganicEnglishRepository repository;

    @Override
    protected InorganicRepository<InorganicEnglishModel> getRepository() {
        return repository;
    }
}
