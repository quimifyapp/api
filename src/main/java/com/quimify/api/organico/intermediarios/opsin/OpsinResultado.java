package com.quimify.api.organico.intermediarios.opsin;

import uk.ac.cam.ch.wwmm.opsin.*;

public class OpsinResultado {

    private final String smiles; // Especie de fórmula más técnica
    private final String cml; // Chemical Markup Language

    // Constructores:

    public OpsinResultado(es.opsin.OpsinResult opsin_result) {
        smiles = opsin_result.getSmiles();
        cml = opsin_result.getCml();
    }

    public OpsinResultado(OpsinResult opsin_result) {
        smiles = opsin_result.getSmiles();
        cml = opsin_result.getCml();
    }

    // Métodos get:

    public String getSmiles() {
        return smiles;
    }

    public String getCml() {
        return cml;
    }

}
