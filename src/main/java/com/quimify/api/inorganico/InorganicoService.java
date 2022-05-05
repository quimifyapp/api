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
import java.util.Optional;

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

    // ADMIN --------------------------------------------------------------------------

    public InorganicoResultado probarPaginaFQ(String direccion) { // TEST
        try {
            HttpURLConnection conexion = (HttpURLConnection) new URL(direccion).openConnection();
            conexion.setRequestProperty("User-Agent", configuracionService.getUserAgent());

            PaginaFQ pagina = new PaginaFQ(descargarTexto(conexion));
            InorganicoModel inorganico = pagina.escanearInorganico();

            return new InorganicoResultado(inorganico);
        } catch (Exception e) {
            return NO_ENCONTRADO;
        }
    }

    public Optional<InorganicoModel> seleccionar(Integer id) {
        return inorganicoRepository.findById(id);
    }

    public Optional<InorganicoModel> reemplazar(InorganicoModel nuevo) {
        Optional<InorganicoModel> reemplazado;

        reemplazado = inorganicoRepository.findById(nuevo.getId());
        if(reemplazado.isPresent()) { // Si existe
            inorganicoRepository.save(nuevo); // De la DB

            for(int i = 0; i < BUSCABLES.size(); i++) // De la memoria principal para ser buscado
                if(BUSCABLES.get(i).getId().equals(nuevo.getId())) {
                    BUSCABLES.set(i, new InorganicoBuscable(reemplazado.get()));
                    break;
                }
        }

        return reemplazado;
    }

    public Optional<InorganicoModel> insertar(InorganicoModel inorganico) {
        Optional<InorganicoModel> insertado;

        try {
            insertado = Optional.of(inorganicoRepository.save(inorganico)); // En la DB
            BUSCABLES.add(new InorganicoBuscable(insertado.get())); // En memoria principal
        }
        catch (Exception e) { // No se ha podido insertar (probablemente falte un campo)
            insertado = Optional.empty();
        }

        return insertado;
    }

    public Optional<InorganicoModel> eliminar(Integer id) {
        Optional<InorganicoModel> eliminado;

        try {
            eliminado = inorganicoRepository.findById(id);

            inorganicoRepository.deleteById(id); // De la DB
            BUSCABLES.removeIf(i -> id.equals(i.getId())); // De la memoria principal
        }
        catch (Exception e) { // No se ha podido eliminar (probablemente no exista)
            eliminado = Optional.empty();
        }

        return eliminado;
    }

    // SERVIDOR -----------------------------------------------------------------------

    public void cargarSearchables() {
        for(InorganicoModel inorganico : inorganicoRepository.findAllByOrderByBusquedasDesc())
            BUSCABLES.add(new InorganicoBuscable(inorganico));
    }

    private void guardarNuevo(InorganicoModel nuevo) {
        BUSCABLES.add(new InorganicoBuscable( // En memoria principal para ser buscado
                inorganicoRepository.save(nuevo))); // En la DB
    }

    // Incrementa el contador de busquedas de ese inorgánico porque ha sido buscado
    private void registrarBusqueda(Integer id) {
        inorganicoRepository.registrarBusqueda(id);
    }

    // CLIENTE -----------------------------------------------------------------------

    public InorganicoResultado autoCompletar(String input) {
        input = InorganicoBuscable.normalizar(input);

        for(InorganicoBuscable buscable : BUSCABLES) // Ordenados por nº de búsquedas
            if(buscable.puedeCompletar(input))
                return new InorganicoResultado(inorganicoRepository
                        .encontrarPorId(buscable.getId()));

        return NO_ENCONTRADO;
    }

    public InorganicoResultado buscar(String input, Boolean usuario_premium) { // En construcción
        InorganicoResultado resultado;

        Integer id = buscarDB(input); // Flowchart #0

        // Flowchart #1
        if(id == null) { // No se encuentra en la DB
            String[] resultado_web = null; // Resultado de una búsqueda web
            // [0]: identificador suficiente como identificadorSuficiente(/titulo del resultado/)
            // [1]: dirección del resultado como "www.fq.com/H2O"

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
            if(resultado_web != null) { // Se ha podido buscar con Google o Bing
                id = buscarDB(resultado_web[0]); // Flowchart #0

                // Flowchart #5
                if(id == null) // No estaba en la DB, es nuevo
                    resultado = escanearFQ(resultado_web[1]);
                // Flowchart #6
                else { // Ya estaba en la DB
                    resultado = decidirPremium(id, usuario_premium);
                    registrarBusqueda(id);
                }
            }
            // Flowchart #7
            else { // No se ha podido buscar ni con Google ni con Bing
                // ...
                resultado = NO_ENCONTRADO; // Test
            }
        }
        // Flowchart #6
        else { // Está en la DB
            resultado = decidirPremium(id, usuario_premium);
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
        }
        catch (Exception e) {
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

            resultado_web = new String[] {identificadorSuficiente(resultado.getString("title")),
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
        }
        catch (Exception e) {
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

            resultado_web = new String[] {identificadorSuficiente(resultado.getString("name")),
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
    // Ej.: "H2O / óxido de dihidrógeno" -> "H2O"
    // Ej.: "metanol - www.fq.com" -> "metanol"
    private String identificadorSuficiente(String titulo) {
        int espacio = titulo.indexOf(' ');
        if(espacio > 0)
            titulo = titulo.substring(0, titulo.indexOf(' '));

        return titulo;
    }

    // Flowchart #5
    private InorganicoResultado escanearFQ(String direccion) {
        InorganicoResultado resultado;

        try {
            HttpURLConnection conexion = (HttpURLConnection) new URL(direccion).openConnection();
            conexion.setRequestProperty("User-Agent", configuracionService.getUserAgent());
            PaginaFQ pagina_fq = new PaginaFQ(descargarTexto(conexion));

            InorganicoModel escaneado = pagina_fq.escanearInorganico();
            if(escaneado != null) {
                if(pagina_fq.getEscaneadoCorrecto())
                    guardarNuevo(escaneado);

                resultado = new InorganicoResultado(escaneado);
            }
            else resultado = NO_ENCONTRADO;
        }
        catch (Exception e) {
            // ...
            resultado = NO_ENCONTRADO;
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
