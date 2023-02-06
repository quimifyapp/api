package com.quimify.api.inorganic;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// This class implements connections to the DB automatically thanks to the JPA library.

@Repository
interface InorganicRepository extends CrudRepository<InorganicModel, Integer> {

    List<InorganicModel> findAllByOrderBySearchCountDesc();

}
