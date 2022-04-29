package com.quimify.api.configuracion;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// Esta clase implementa conexiones con la DB automáticamente gracias a la librería JPA.

@Repository
public interface ConfiguracionRepository extends CrudRepository<ConfiguracionModel, Integer> {

    // --------------------------------------------------------------------------------

    @Query(value = "SELECT * FROM configuracion WHERE version = :version LIMIT 1",
            nativeQuery = true) // MySQL
    ConfiguracionModel encontrarPorVersion(@Param("version") Integer version);
}
