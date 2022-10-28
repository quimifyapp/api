package com.quimify.api.metrics;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

// Esta clase implementa conexiones con la DB automáticamente gracias a la librería JPA.

@Repository
interface MetricsRepository extends CrudRepository<MetricsModel, Date> {

}
