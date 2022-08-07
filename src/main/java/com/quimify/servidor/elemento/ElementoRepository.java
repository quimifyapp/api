package com.quimify.servidor.elemento;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Esta clase implementa conexiones con la DB automáticamente gracias a la librería JPA.

@Repository
public interface ElementoRepository extends CrudRepository<ElementoModel, Integer> {

    Optional<ElementoModel> findBySimbolo(String simbolo);

}
