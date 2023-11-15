package com.quimify.api.client;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// This class implements connections to the DB automatically thanks to the JPA library.

@Repository
interface ClientRepository extends CrudRepository<ClientModel, ClientId> {

    Optional<ClientModel> findByPlatformAndVersion(String platform, Integer version);

}
