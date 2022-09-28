package com.quimify.api.organic.compounds;

// Esta interfaz implementa compuestos orgánicos no cíclicos:
//  - Simple: R
//  - Éter: R - O - R' (con funciones de prioridad menor a la función éter)
//  - Éster: TODO

import com.quimify.api.organic.components.FunctionalGroup;
import com.quimify.api.organic.components.Substituent;

import java.util.List;

public interface OpenChain {
    OpenChain getReversed();

    boolean isDone();

    int getFreeBonds();

    List<FunctionalGroup> getOrderedBondableGroups();

    void bondCarbon();

    void bond(Substituent substituent);

    void bond(FunctionalGroup functionalGroup);

    void correctSubstituents();

    String getName();

    String getStructure();
}