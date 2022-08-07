package com.quimify.servidor.inorganico;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Esta clase implementa conexiones con la DB automáticamente gracias a la librería JPA.

@Repository
public interface InorganicoRepository extends CrudRepository<InorganicoModel, Integer> {

    List<InorganicoModel> findAllByOrderByBusquedasDesc();

}
