package com.quimify.api.inorganico;

import com.quimify.api.configuracion.ConfiguracionService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

        for(InorganicoBuscable buscable : BUSCABLES) // Ordenados por nº de búsquedas
            if(buscable.puedeCompletar(input))
                return new InorganicoResultado(inorganicoRepository
                        .encontrarPorId(buscable.getId()));

        return NO_ENCONTRADO;
    }

    // TODO: Terminar esta función
    private void guardar(InorganicoModel inorganico) {
        BUSCABLES.add(new InorganicoBuscable( // En memoria para ser buscado
                inorganicoRepository.save(inorganico))); // En la DB
    }

    public InorganicoResultado buscar(String input, Boolean usuario_premium) { // En construcción
        InorganicoResultado resultado;

        Integer id = buscarDB(input); // Flowchart #0

        // Flowchart #1
        if(id == null) {
            String[] resultado_web = null; // [0]: identificador suficiente (suele ser la fórmula)
            // [1]: URL del resultado de la búsqueda ("www.fq.com/H2O")

            // Flowchart #2
            if(configuracionService.getGoogleON() /*&& limite */)
                resultado_web = buscarGoogle(input);

            // Flowchart #3
            if(resultado_web == null && configuracionService.getBingGratisON())
                resultado_web = buscarBingGratis(input);

            // Flowchart #4
            if(resultado_web == null && configuracionService.getBingDePagoON() /*&& limite */)
                resultado_web = buscarBingDePago(input);

            // Flowchart #0 ó #5
            if(resultado_web != null) {
                id = buscarDB(resultado_web[0]); // Flowchart #0

                if(id != null)
                    resultado = decidirPremium(id, usuario_premium); // Flowchart #6
                else { // Flowchart #5
                    resultado = NO_ENCONTRADO;
                }
            }
            else { // Flowchart #7
                // ...
                resultado = NO_ENCONTRADO;
            }
        }
        else {
            resultado = decidirPremium(id, usuario_premium); // Flowchart #6
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
    private String[] buscarGoogle(String input) {
        String[] resultado_web;

        try {
            URL url = new URL(configuracionService.getGoogleURL() +
                    formatearHTTP(input)); // Parámetro HTTP de búsqueda
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");
            conexion.setRequestProperty("Accept", "application/json");

            JSONObject respuesta = descargarJSON(conexion);

            if(respuesta.getJSONObject("searchInformation").getInt("totalResults") > 0) {
                JSONObject resultado = respuesta.getJSONArray("items").getJSONObject(0);

                String titulo = resultado.getString("title"); // "H2O / óxido de dihidrógeno"
                titulo = titulo.substring(0, titulo.indexOf('/') - 1); // "H2O"
                String direccion = resultado.getString("formattedUrl"); // "www.fq.com/..."

                resultado_web = new String[] {titulo, direccion};
            }
            else resultado_web = null;
        } catch (Exception e) {
            // Error con la URL, la conexión o HTTP
            // ...
            resultado_web = null;
        }

        return resultado_web;
    }

    // Flowchart #3
    private String[] buscarBingGratis(String input) {
        return buscarBing(input, configuracionService.getBingGratisKey());
    }

    // Flowchart #4
    private String[] buscarBingDePago(String input) {
        return buscarBing(input, configuracionService.getBingDePagoKey());
    }

    // Flowchart #3 ó #4
    private String[] buscarBing(String input, String key) {
        String[] resultado_web;

        try {
            URL url = new URL(configuracionService.getBingURL() +
                    formatearHTTP(input)); // Parámetro HTTP de búsqueda
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestProperty("Ocp-Apim-Subscription-Key", key);

            JSONObject respuesta = descargarJSON(conexion);

            if(false) {

            }
            else resultado_web = null;
        } catch (Exception e) {
            // Error con la URL, la conexión o HTTP
            // ...
            resultado_web = null;
        }

        return resultado_web;
    }

    // Flowchart #2 ó #3 ó #4
    private String formatearHTTP(String input) {
        return URLEncoder.encode(input, StandardCharsets.UTF_8);
    }

    // Flowchart #2 ó #3 ó #4
    private JSONObject descargarJSON(HttpURLConnection conexion) throws Exception {
        BufferedReader br = new BufferedReader(
                new InputStreamReader((conexion.getInputStream())));

        String temp;
        StringBuilder texto = new StringBuilder();
        while ((temp = br.readLine()) != null)
            texto.append(temp);

        conexion.disconnect();
        return new JSONObject(texto.toString());
    }

    // Flowchart #7
    private InorganicoResultado decidirPremium(Integer id, Boolean premium) {
        InorganicoModel resultado = inorganicoRepository.encontrarPorId(id);

        return (!resultado.getPremium() || premium)
                ? new InorganicoResultado(resultado) : NO_PREMIUM;
    }

}
