package com.quimify.api.report;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// This class implements connections to the DB automatically thanks to the JPA library.

@Repository
interface ReportRepository extends CrudRepository<ReportModel, Integer> {

}
