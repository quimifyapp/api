package com.quimify.api.configuracion;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// Esta clase implementa conexiones con la DB automáticamente gracias a la librería JPA.

@Repository
public interface ConfiguracionRepository extends CrudRepository<ConfiguracionModel, Integer> {

    ConfiguracionModel findByVersion(Integer version);

}