package com.quimify.api.access_data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// This class implements connections to the DB automatically thanks to the JPA library.

@Repository
interface AccessDataRepository extends CrudRepository<AccessDataModel, Integer> {

    AccessDataModel findByClientVersion(Integer clientVersion);

}
