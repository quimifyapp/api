package com.quimify.api.element;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Esta clase implementa conexiones con la DB automáticamente gracias a la librería JPA.

@Repository
public interface ElementRepository extends CrudRepository<ElementModel, Integer> {

    Optional<ElementModel> findBySymbol(String symbol);

}
