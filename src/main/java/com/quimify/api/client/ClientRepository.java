package com.quimify.api.client;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// Esta clase implementa conexiones con la DB automáticamente gracias a la librería JPA.

@Repository
public interface ClientRepository extends CrudRepository<ClientModel, Integer> {

    ClientModel findByVersion(Integer version);

}
