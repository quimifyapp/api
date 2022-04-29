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

    public ArrayList<InorganicoModel> obtenerTodos() { // TEST
        return (ArrayList<InorganicoModel>) inorganicoRepository.findAll();
    }

    public InorganicoModel insertarInorganico(InorganicoModel inorganico) {  // TEST
        InorganicoModel insertado = inorganicoRepository.save(inorganico);
        BUSCABLES.add(new InorganicoBuscable(insertado));

        return insertado;
    }

    // --------------------------------------------------------------------------------

    public void cargarSearchables() {
        for(InorganicoModel inorganico : inorganicoRepository.findAllByOrderByBusquedasDesc())
            BUSCABLES.add(new InorganicoBuscable(inorganico));
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

    public InorganicoResultado buscar(String input, Boolean usuario_premium) { // En construcción
        InorganicoResultado resultado;

        Integer id = buscarDB(input); // Flowchart #0

        // Flowchart #1
        if(id == null) {
            String[] resultado_web; // [0]: un identificador suficiente (generalmente la fórmula)
            // [1]: la URL del resultado de la búsqueda ("www.fq.com/H2O")

            // Flowchart #2
            if(configuracionService.getApiGoogleON() /*&& limite no superado*/)
                resultado_web = buscarApiGoogle(input);
            // Flowchart #3
            else if(configuracionService.getApiBingGratisON())
                resultado_web = buscarApiBingGratis(input);
            // Flowchart #4
            else if(configuracionService.getApiBingDePagoON() /*&& limite no superado*/)
                resultado_web = buscarApiBingDePago(input);

            // Flowchart #7
            else {
                // ...
            }

            id = buscarDB(resultado_web[0]); // Flowchart #0

            // ...

            resultado = NO_ENCONTRADO;
        }
        else {
            resultado = decidirPremium(id, usuario_premium); // Flowchart #7
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

    // Flowchart #2
    private String[] buscarApiGoogle(String input) {
        String URL = configuracionService.getApiGoogleURL() + // Parte no variable
                formatearHTTP(input); // Parámetro HTTP de búsqueda

        // ...
    }

    // Flowchart #3
    private String[] buscarApiBingGratis(String input) {
        return buscarApiBing(configuracionService.getApiBingGratisURL() + // Parte no variable
                formatearHTTP(input)); // Parámetro HTTP de búsqueda
    }

    // Flowchart #4
    private String[] buscarApiBingDePago(String input) {
        return buscarApiBing(configuracionService.getApiBingDePagoURL() + // Parte no variable
                formatearHTTP(input)); // Parámetro HTTP de búsqueda
    }

    // Flowchart #3 ó #4
    private String[] buscarApiBing(String URL) {

        // ...
    }

    // Flowchart #2 ó #3 ó #4
    private String formatearHTTP(String input) {
        return input.replaceAll(" ", "+"); // Mejor "%20" en vez de "+"?
        // URLEncoder.encode(q, StandardCharsets.UTF_8);?
    }

    // Flowchart #7
    private InorganicoResultado decidirPremium(Integer id, Boolean premium) {
        InorganicoModel resultado = inorganicoRepository.encontrarPorId(id);

        return (!resultado.getPremium() || premium)
                ? new InorganicoResultado(resultado) : NO_PREMIUM;
    }

}
