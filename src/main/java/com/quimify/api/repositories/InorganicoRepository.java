package com.quimify.api.repositories;

// Esta clase realiza conexiones automáticas con la DB.

// Inorganico = (id, busquedas, premium, formula, nombre, alternativo, masa, densidad, fu., eb.)

import com.quimify.api.models.InorganicoModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface InorganicoRepository extends CrudRepository<InorganicoModel, Integer> {
    ArrayList<InorganicoModel> findByFormula(String formula);
}
