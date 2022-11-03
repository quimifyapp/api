package com.quimify.api.reporte;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// Esta clase implementa conexiones con la DB automáticamente gracias a la librería JPA.

@Repository
interface ReportRepository extends CrudRepository<ReportModel, Integer> {

}