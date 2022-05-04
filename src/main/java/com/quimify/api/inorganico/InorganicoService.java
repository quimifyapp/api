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
    ConfiguracionService configuracionService; // Procesos de la configuración

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
    private void guardarNuevo(InorganicoModel inorganico) {
        // ver que no está repe
        // si lo está, sumar

        BUSCABLES.add(new InorganicoBuscable( // En memoria para ser buscado
                inorganicoRepository.save(inorganico))); // En la DB
    }

    private void registrarBusqueda(Integer id) {
        inorganicoRepository.registrarBusqueda(id);
    }

    public InorganicoResultado buscar(String input, Boolean usuario_premium) { // En construcción
        InorganicoResultado resultado;

        // Flowchart #0
        Integer id = buscarDB(input);

        // Flowchart #1
        if(id == null) {
            String[] resultado_web = null; // Resultado de una búsqueda web
            // [0]: identificador suficiente (suele ser la fórmula)
            // [1]: dirección del resultado ("www.fq.com/H2O")

            // Flowchart #2
            if(configuracionService.getGoogleON() /*&& limite */)
                resultado_web = tryBuscarGoogle(input);

            // Flowchart #3
            if(resultado_web == null && configuracionService.getBingGratisON())
                resultado_web = tryBuscarBing(input, configuracionService.getBingGratisKey());

            // Flowchart #4
            if(resultado_web == null && configuracionService.getBingPagoON() /*&& limite */)
                resultado_web = tryBuscarBing(input, configuracionService.getBingPagoKey());

            // Flowchart #0 ó #5
            if(resultado_web != null) {
                // Flowchart #0
                id = buscarDB(resultado_web[0]); // Identificador suficiente

                if(id != null) {
                    resultado = decidirPremium(id, usuario_premium); // Flowchart #6
                    registrarBusqueda(id);
                }
                else { // Flowchart #5
                    InorganicoModel nuevo = tryParsearFQ(resultado_web[1]);

                    if(nuevo != null) {
                        resultado = (!nuevo.getPremium() || usuario_premium)
                                ? new InorganicoResultado(nuevo) : NO_PREMIUM;
                        // guardar el nuevo
                    }
                    else resultado = NO_ENCONTRADO;
                }
            }
            else { // Flowchart #7
                // ...
                resultado = NO_ENCONTRADO; // Test
            }
        }
        else {
            resultado = decidirPremium(id, usuario_premium); // Flowchart #6
            registrarBusqueda(id);
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
    private String[] tryBuscarGoogle(String input) {
        String[] resultado_web;

        try {
            resultado_web = buscarGoogle(input);
        } catch (Exception e) {
            // ...
            resultado_web = null;
        }

        return resultado_web;
    }

    // Flowchart #2
    private String[] buscarGoogle(String input) throws Exception {
        String[] resultado_web;

        HttpURLConnection conexion = (HttpURLConnection) new URL(
                configuracionService.getGoogleURL() + formatearHTTP(input)).openConnection();
        conexion.setRequestMethod("GET");
        conexion.setRequestProperty("Accept", "application/json");

        JSONObject respuesta = new JSONObject(descargarTexto(conexion));

        if(respuesta.getJSONObject("searchInformation").getInt("totalResults") > 0) {
            JSONObject resultado = respuesta.getJSONArray("items").getJSONObject(0);

            resultado_web = new String[] {identificador(resultado.getString("title")),
                    resultado.getString("formattedUrl")}; // "www.fq.com/..."
        }
        else resultado_web = null;

        return resultado_web;
    }

    // Flowchart #3 ó #4
    private String[] tryBuscarBing(String input, String key) {
        String[] resultado_web;

        try {
            resultado_web = buscarBing(input, key);
        } catch (Exception e) {
            // ...
            resultado_web = null;
        }

        return resultado_web;
    }

    // Flowchart #3 ó #4
    private String[] buscarBing(String input, String key) throws Exception {
        String[] resultado_web;

        HttpURLConnection conexion = (HttpURLConnection) new URL(
                configuracionService.getBingURL() + formatearHTTP(input)).openConnection();
        conexion.setRequestProperty("Ocp-Apim-Subscription-Key", key);

        JSONObject respuesta = new JSONObject(descargarTexto(conexion));

        if(respuesta.has("webPages")) {
            JSONObject resultado = respuesta.getJSONObject("webPages")
                    .getJSONArray("value").getJSONObject(0);

            resultado_web = new String[] {identificador(resultado.getString("name")),
                    resultado.getString("url")}; // "www.fq.com/..."
        }
        else resultado_web = null;

        return resultado_web;
    }

    // Flowchart #2 ó #3 ó #4
    private String formatearHTTP(String input) {
        return URLEncoder.encode(input, StandardCharsets.UTF_8);
    }

    // Flowchart #2 ó #3 ó #4
    private String identificador(String titulo) {
        // "H2O / óxido de dihidrógeno", "metanol - www.fq.com", "etanol"...
        int espacio = titulo.indexOf(' ');
        if(espacio > 0)
            titulo = titulo.substring(0, titulo.indexOf(' ')); // "H2O", "metanol", "etanol"...

        return titulo;
    }

    // Flowchart #5
    private InorganicoModel tryParsearFQ(String direccion) {
        InorganicoModel resultado;

        try {
            HttpURLConnection conexion = (HttpURLConnection) new URL(direccion).openConnection();
            conexion.setRequestProperty("User-Agent", configuracionService.getUserAgent());
            PaginaFQ pagina_fq = new PaginaFQ(descargarTexto(conexion));

            resultado = pagina_fq.nuevoInorganico();
        } catch (Exception e) {
            // ...
            resultado = null;
        }

        return resultado;
    }

    // Flowchart #2 ó #3 ó #4 ó #5
    private String descargarTexto(HttpURLConnection conexion) throws Exception {
        BufferedReader descarga = new BufferedReader(
                new InputStreamReader(conexion.getInputStream()));

        String linea;
        StringBuilder texto = new StringBuilder();
        while((linea = descarga.readLine()) != null)
            texto.append(linea);

        descarga.close();
        conexion.disconnect();

        return texto.toString();
    }

    // Flowchart #7
    private InorganicoResultado decidirPremium(Integer id, Boolean usuario_premium) {
        InorganicoModel resultado = inorganicoRepository.encontrarPorId(id);

        return (!resultado.getPremium() || usuario_premium)
                ? new InorganicoResultado(resultado) : NO_PREMIUM;
    }

}
