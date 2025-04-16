package com.quimify.api.inorganic;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface InorganicRepository<T extends InorganicModel>
        extends CrudRepository<T, Integer> {

    // Example custom query method
    List<T> findAllByOrderBySearchesDesc();
}

