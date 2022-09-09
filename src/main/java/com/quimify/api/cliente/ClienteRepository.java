package com.quimify.api.cliente;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// Esta clase implementa conexiones con la DB automáticamente gracias a la librería JPA.

@Repository
public interface ClienteRepository extends CrudRepository<ClienteModel, Integer> {

    ClienteModel findByVersion(Integer version);

}
