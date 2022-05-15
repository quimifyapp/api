package com.quimify.api.elemento;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// Esta clase implementa conexiones con la DB automáticamente gracias a la librería JPA.

@Repository
public interface ElementoRepository extends CrudRepository<ElementoModel, Integer> {

}
