package com.quimify.api.correction;

// This class implements connections to the DB automatically thanks to the JPA library.

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface CorrectionRepository extends CrudRepository<CorrectionModel, Integer> {

    List<CorrectionModel> findAllByOrderByPriority();

}

