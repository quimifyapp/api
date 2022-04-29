package com.quimify.api.inorganico;

import com.quimify.api.configuracion.ConfiguracionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

// Esta clase procesa los compuestos inorgánicos.

@Service
public class InorganicoService {

    @Autowired
    private InorganicoRepository inorganicoRepository; // Conexión con la DB

    @Autowired
    ConfiguracionService configuracionService;  // Procesos de la configuración

    private static final ArrayList<InorganicoBuscable> BUSCABLES = new ArrayList<>();

    private static final InorganicoResultado NO_ENCONTRADO = // Eso, o se ha producido un error
            new InorganicoResultado(InorganicoResultado.NO_ENCONTRADO);

    private static final InorganicoResultado NO_PREMIUM = // Compuesto premium y usuario no-premium
            new InorganicoResultado(InorganicoResultado.NO_PREMIUM);

    // --------------------------------------------------------------------------------

    public ArrayList<InorganicoModel> obtenerTodos() { // Test
        return (ArrayList<InorganicoModel>) inorganicoRepository.findAll();
    }

    public InorganicoModel insertarInorganico(InorganicoModel inorganico) {  // Test
        InorganicoModel insertado = inorganicoRepository.save(inorganico);
        BUSCABLES.add(new InorganicoBuscable(insertado));

        return insertado;
    }

    public InorganicoResultado autoCompletar(String input) {
        input = InorganicoBuscable.normalizar(input);

        for(InorganicoBuscable ejemplar : BUSCABLES) // Ordenados por nº de búsquedas
            if(ejemplar.puedeCompletar(input))
                return new InorganicoResultado(inorganicoRepository.encontrarPorId(ejemplar.getId()));

        return NO_ENCONTRADO;
    }

    private void guardar(InorganicoModel inorganico) { // En construcción
        BUSCABLES.add(new InorganicoBuscable( // En memoria para ser buscado
                inorganicoRepository.save(inorganico))); // En la DB
    }

    public InorganicoResultado buscar(String input, Boolean premium) { // En construcción
        InorganicoResultado resultado;

        Integer id = buscarDB(input); // Flowchart #0

        // Flowchart #1
        if(id == null) {
            // resultado_web[0]: un identificador suficiente (nombre, fórmula...)
            // resultado_web[1]: la URL del resultado de la búsqueda

            String[] resultado_web = new String[2];

            String url = configuracionService.getApiGoogleURL(); // Test

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
        input = InorganicoBuscable.normalizar(input);

        for(InorganicoBuscable ejemplar : BUSCABLES) // Ordenados por nº de búsquedas
            if(ejemplar.coincide(input))
                return ejemplar.getId();

        return null;
    }

    /*private String[] buscarApiGoogle(String input) {

    }*/

    // Flowchart #7
    private InorganicoResultado decidirPremium(Integer id, Boolean premium) {
        InorganicoModel resultado = inorganicoRepository.encontrarPorId(id);

        return (!resultado.getPremium() || premium)
                ? new InorganicoResultado(resultado) : NO_PREMIUM;
    }

    public void cargarSearchables() {
        for(InorganicoModel inorganico : inorganicoRepository.findAllByOrderByBusquedasDesc())
            BUSCABLES.add(new InorganicoBuscable(inorganico));
    }

}
