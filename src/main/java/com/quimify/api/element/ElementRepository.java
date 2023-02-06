package com.quimify.api.element;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// This class implements connections to the DB automatically thanks to the JPA library.

@Repository
interface ElementRepository extends CrudRepository<ElementModel, Integer> {

    Optional<ElementModel> findBySymbol(String symbol);

}
