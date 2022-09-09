package com.quimify.api.organico.componentes;

public enum Funciones { // En orden de prioridad:
    acido,      // =O, -OH
    carboxil,   // -COOH ácido no principal
    amida,      // =O, -NH2
    carbamoil,  // -CONH2 amida no principal
    nitrilo,    // ≡N
    cianuro,    // -CN nitrilo no principal
    aldehido,   // =O, -H
    cetona,     // =O
    alcohol,    // -OH
    amina,      // -NH2
    eter,       // -O-R'
    alqueno,    // = doble enlace
    alquino,    // ≡ triple enlace
    nitro,      // -NO2
    bromo,      // -Br
    cloro,      // -Cl
    fluor,      // -F
    yodo,       // -I
    radical,    // -R
    hidrogeno   // -H
}
