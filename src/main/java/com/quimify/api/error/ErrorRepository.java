package com.quimify.api.error;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// This class implements DB connections automatically thanks to JPA library.

@Repository
interface ErrorRepository extends CrudRepository<ErrorModel, Integer> {

}
