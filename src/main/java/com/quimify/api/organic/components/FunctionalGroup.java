package com.quimify.api.organic.components;

// This enum represents functional groups.

public enum FunctionalGroup { // En orden de prioridad:
    acid,      // =O, -OH
    carboxyl,   // -COOH ácido no principal
    amide,      // =O, -NH2
    carbamoyl,  // -CONH2 amida no principal
    nitrile,    // ≡N
    cyanide,    // -CN nitrilo no principal
    aldehyde,   // =O, -H
    ketone,     // =O
    alcohol,    // -OH
    amine,      // -NH2
    ether,       // -O-R'
    alkene,    // = doble enlace
    alkyne,    // ≡ triple enlace
    nitro,      // -NO2
    bromine,      // -Br
    chlorine,      // -Cl
    fluorine,      // -F
    iodine,       // -I
    radical,    // -R
    hydrogen   // -H
}
