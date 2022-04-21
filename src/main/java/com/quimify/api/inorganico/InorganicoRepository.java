package com.quimify.api.inorganico;

// Esta clase implementa conexiones con la DB automáticamente gracias a la librería JPA.

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Repository
public interface InorganicoRepository extends CrudRepository<InorganicoModel, Integer> {
    ArrayList<InorganicoModel> findByFormulaOrderByBusquedasDesc(String formula); // Test

    @Modifying
    @Transactional
    @Query("UPDATE InorganicoModel i SET i.busquedas = i.busquedas + 1 " +
            "WHERE i.id = :#{#inorganico.id}")
    void incrementarBusquedas(@Param("inorganico") InorganicoModel inorganico);
}
