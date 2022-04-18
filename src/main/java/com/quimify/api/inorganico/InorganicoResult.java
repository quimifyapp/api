package com.quimify.api.inorganico;

public class InorganicoResult {

    final private Integer resultado;
    final private InorganicoModel inorganico;

    public final static Integer NO_ENCONTRADO = 0;
    public final static Integer ENCONTRADO = 1;
    public final static Integer PREMIUM = 2;

    public InorganicoResult(InorganicoModel inorganico) {
        resultado = ENCONTRADO;
        this.inorganico = inorganico;
    }

    public InorganicoResult(Integer resultado) {
        this.resultado = resultado;
        inorganico = null;
    }

    // Getters:

    public Integer getResultado(){
        return resultado;
    }

    public InorganicoModel getInorganico() {
        return inorganico;
    }

}
