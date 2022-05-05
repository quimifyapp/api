package com.quimify.api.inorganico;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

// Esta clase implementa conexiones con la DB automáticamente gracias a la librería JPA.

@Repository
public interface InorganicoRepository extends CrudRepository<InorganicoModel, Integer> {

    ArrayList<InorganicoModel> findAllByOrderByBusquedasDesc();

    // --------------------------------------------------------------------------------
    // TODO: Cambiar esto?
    @Query(value = "SELECT * FROM inorganico WHERE id = :id LIMIT 1", nativeQuery = true) // MySQL
    InorganicoModel encontrarPorId(@Param("id") Integer id);

    @Modifying // Modifica la DB
    @Transactional // Por seguridad
    @Query("UPDATE InorganicoModel i SET i.busquedas = i.busquedas + 1 WHERE i.id = :id") // MySQL
    void registrarBusqueda(@Param("id") Integer id);
}
