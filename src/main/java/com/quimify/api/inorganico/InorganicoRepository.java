package com.quimify.api.inorganico;

// Esta clase implementa conexiones con la DB. automáticamente gracias a la librería JPA.

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface InorganicoRepository extends CrudRepository<InorganicoModel, Integer> {
    ArrayList<InorganicoModel> findByFormula(String formula);
}
