package com.quimify.api.inorganic;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Esta clase implementa conexiones con la DB automáticamente gracias a la librería JPA.

@Repository
interface InorganicRepository extends CrudRepository<InorganicModel, Integer> {

    List<InorganicModel> findAllByOrderBySearchCountDesc();

    Optional<InorganicModel> findBySearchTagsContaining(InorganicSearchTagModel inorganicSearchTagModel);

}
