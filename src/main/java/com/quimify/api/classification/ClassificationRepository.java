package com.quimify.api.classification;

// This class implements connections to the DB automatically thanks to the JPA library.

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface ClassificationRepository extends CrudRepository<ClassificationModel, Integer> {

    List<ClassificationModel> findAllByOrderByPriority();

}
