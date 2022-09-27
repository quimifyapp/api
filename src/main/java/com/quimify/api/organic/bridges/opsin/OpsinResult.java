package com.quimify.api.organic.bridges.opsin;

public class OpsinResult {

    private final String smiles; // Especie de fórmula más técnica
    private final String cml; // Chemical Markup Language

    // Constructores:

    public OpsinResult(es.opsin.OpsinResult opsin_result) {
        smiles = opsin_result.getSmiles();
        cml = opsin_result.getCml();
    }

    public OpsinResult(uk.ac.cam.ch.wwmm.opsin.OpsinResult opsin_result) {
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
