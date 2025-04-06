package com.quimify.api.inorganic;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InorganicRepository<InorganicModel extends com.quimify.api.inorganic.InorganicModel>
        extends CrudRepository<InorganicModel, Integer> {

    List<InorganicModel> findAllByOrderBySearchesDesc();

}
