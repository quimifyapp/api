package com.quimify.api.configuracion;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// Esta clase implementa conexiones con la DB automáticamente gracias a la librería JPA.

@Repository
public interface ConfiguracionRepository extends CrudRepository<ConfiguracionModel, Integer> {

    //TODO: reemplazar por los métodos JPA?

    String CONDICION = " FROM configuracion WHERE version = :version LIMIT 1";

    @Query(value = "SELECT google_on" + CONDICION, nativeQuery = true) // MySQL
    Boolean encontrarGoogleON(@Param("version") Integer version);

    @Query(value = "SELECT google_url" + CONDICION, nativeQuery = true) // MySQL
    String encontrarGoogleURL(@Param("version") Integer version);

    @Query(value = "SELECT bing_url" + CONDICION, nativeQuery = true) // MySQL
    String encontrarBingURL(@Param("version") Integer version);

    @Query(value = "SELECT bing_gratis_on" + CONDICION, nativeQuery = true) // MySQL
    Boolean encontrarBingGratisON(@Param("version") Integer version);

    @Query(value = "SELECT bing_gratis_key" + CONDICION, nativeQuery = true) // MySQL
    String encontrarBingGratisKey(@Param("version") Integer version);

    @Query(value = "SELECT bing_pago_on" + CONDICION, nativeQuery = true) // MySQL
    Boolean encontrarBingPagoON(@Param("version") Integer version);

    @Query(value = "SELECT bing_pago_key" + CONDICION, nativeQuery = true) // MySQL
    String encontrarBingPagoKey(@Param("version") Integer version);

    @Query(value = "SELECT user_agent" + CONDICION, nativeQuery = true) // MySQL
    String encontrarUserAgent(@Param("version") Integer version);

}
