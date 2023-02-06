package com.quimify.api.client_error;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// This class implements connections to the DB automatically thanks to the JPA library.

@Repository
interface ClientErrorRepository extends CrudRepository<ClientErrorModel, Integer> {

}
