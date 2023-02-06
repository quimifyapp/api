package com.quimify.api.metrics;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

// This class implements connections to the DB automatically thanks to the JPA library.

@Repository
interface MetricsRepository extends CrudRepository<MetricsModel, Date> {

}
