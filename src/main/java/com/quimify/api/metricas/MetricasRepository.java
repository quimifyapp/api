package com.quimify.api.metricas;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

// Esta clase implementa conexiones con la DB automáticamente gracias a la librería JPA.

@Repository
public interface MetricasRepository extends CrudRepository<MetricasModel, Date> {

}
