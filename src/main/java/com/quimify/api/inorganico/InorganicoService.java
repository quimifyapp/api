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

    public void guardar(InorganicoModel inorganico) { // En construcción
        searchables.add(new InorganicoSearchable( // En memoria para ser buscado
                inorganicoRepository.save(inorganico))); // En la DB
    }

    public InorganicoResult buscar(String input, Boolean premium) { // En construcción
        InorganicoResult resultado;

        try {
            ArrayList<InorganicoModel> identicos =
                    inorganicoRepository.findByFormulaOrderByBusquedasDesc(input);

            if(identicos.size() != 0) {
                InorganicoModel encontrado = identicos.get(0);
                if(!encontrado.getPremium() || premium) {
                    resultado = new InorganicoResult(encontrado);
                    inorganicoRepository.incrementarBusquedas(encontrado); // Su contador

                    // Como si fuera uno nuevo:
                    /* InorganicoModel prueba = new InorganicoModel();
                    prueba.setFormula(encontrado.getFormula());
                    prueba.setNombre(encontrado.getNombre());
                    guardar(prueba); */
                }
                else resultado = NO_PREMIUM;
            }
            else resultado = NO_ENCONTRADO;
        } catch (Exception error) {
            // ...
            resultado = NO_ENCONTRADO;
        }

        return resultado;
    }

}
