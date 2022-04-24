package com.quimify.api.inorganico;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

// Esta clase procesa los compuestos inorgánicos y realiza conexiones con la DB.

@Service
public class InorganicoService {
    @Autowired
    InorganicoRepository inorganicoRepository; // Conexión con la DB

    public static ArrayList<InorganicoSearchable> searchables = new ArrayList<>();

    private final static InorganicoResult NO_ENCONTRADO = // Eso, o se ha producido un error
            new InorganicoResult(InorganicoResult.NO_ENCONTRADO);

    private final static InorganicoResult NO_PREMIUM = // Compuesto premium y usuario no-premium
            new InorganicoResult(InorganicoResult.NO_PREMIUM);

    // --------------------------------------------------------------------------------

    public ArrayList<InorganicoModel> obtenerTodos() { // Test
        return (ArrayList<InorganicoModel>) inorganicoRepository.findAll();
    }

    public InorganicoModel insertarInorganico(InorganicoModel inorganico) {  // Test
        InorganicoModel insertado = inorganicoRepository.save(inorganico);
        searchables.add(new InorganicoSearchable(insertado));

        return insertado;
    }

    public InorganicoResult autoCompletar(String input) {
        input = InorganicoSearchable.normalizar(input);

        for(InorganicoSearchable ejemplar : searchables) // Ordenados por nº de búsquedas
            if(ejemplar.puedeCompletar(input))
                return new InorganicoResult(inorganicoRepository.encontrarPorId(ejemplar.getId()));

        return NO_ENCONTRADO;
    }

    private void guardar(InorganicoModel inorganico) { // En construcción
        searchables.add(new InorganicoSearchable( // En memoria para ser buscado
                inorganicoRepository.save(inorganico))); // En la DB
    }

    public InorganicoResult buscar(String input, Boolean premium) { // En construcción
        InorganicoResult resultado;

        Integer id = buscarDB(input); // Flowchart #0

        // Flowchart #1
        if(id == null) {
            // Flowchart #2,3,4,5,6
            // ...
            resultado = NO_ENCONTRADO;
        }
        else {
            resultado = decidirPremium(id, premium); // Flowchart #7
        }

        return resultado;
    }

    // Flowchart #0
    private Integer buscarDB(String input) {
        input = InorganicoSearchable.normalizar(input);

        for(InorganicoSearchable ejemplar : searchables) // Ordenados por nº de búsquedas
            if(ejemplar.coincide(input))
                return ejemplar.getId();

        return null;
    }

    // Flowchart #7
    private InorganicoResult decidirPremium(Integer id, Boolean premium) {
        InorganicoModel resultado = inorganicoRepository.encontrarPorId(id);

        return (!resultado.getPremium() || premium)
                ? new InorganicoResult(resultado) : NO_PREMIUM;
    }

}
