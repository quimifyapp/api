package com.quimify.api.inorganico;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

// Esta clase implementa conexiones con la DB automáticamente gracias a la librería JPA.

@Repository
public interface InorganicoRepository extends CrudRepository<InorganicoModel, Integer> {

    ArrayList<InorganicoModel> findAllByOrderByBusquedasDesc();

}
