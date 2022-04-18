package com.quimify.api.inorganico;

// Esta clase realiza conexiones automáticas con la DB.

// Inorganico = (id, busquedas, premium, formula, nombre, alternativo, masa, densidad, fu., eb.)

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface InorganicoRepository extends CrudRepository<InorganicoModel, Integer> {
    ArrayList<InorganicoModel> findByFormula(String formula);
}
