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
        Optional<InorganicoModel> reemplazado = inorganicoRepository.findById(nuevo.getId());
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
        Optional<InorganicoModel> insertado = Optional.of(inorganicoRepository.save(inorganico)); // En la DB

        BUSCABLES.add(new InorganicoBuscable(insertado.get())); // En memoria principal

        return insertado;
    }

    public Optional<InorganicoModel> eliminar(Integer id) {
        Optional<InorganicoModel> eliminado = inorganicoRepository.findById(id);

        inorganicoRepository.deleteById(id); // De la DB
        BUSCABLES.removeIf(i -> id.equals(i.getId())); // De la memoria principal

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
        Optional<InorganicoModel> buscado = inorganicoRepository.findById(id);

        if(buscado.isPresent()) {
            buscado.get().registrarBusqueda();
            inorganicoRepository.save(buscado.get());
        }
    }

    // CLIENTE -----------------------------------------------------------------------

    public String autoCompletar(String input) {
        String resultado = "";

        input = InorganicoBuscable.normalizar(input); // Para poder hacer la búsqueda
        for(InorganicoBuscable buscable : BUSCABLES) { // Ordenados por nº de búsquedas
            String complecion = buscable.autoCompletar(input); // Devuelve un keyword solo si puede autocompletar
            if(complecion != null) {
                InorganicoModel encontrado = inorganicoRepository.findById(buscable.getId()).get();

                if(complecion.equals(InorganicoBuscable.normalizar(encontrado.getFormula())))
                    resultado = encontrado.getFormula(); // La fórmula puede autocompletar
                else if(complecion.equals(InorganicoBuscable.normalizar(encontrado.getAlternativo())))
                    resultado = encontrado.getAlternativo(); // El alternativo puede autocompletar
                else resultado = encontrado.getNombre(); // El nobre (o una etiqueta) puede autocompletar

                break;
            }
        }

        return resultado;
    }

    private static class BusquedaWeb {
        // Resultado de una búsqueda web (de cualquier buscador)
        boolean encontrado; // Se ha encontrado algo
        String titulo; // Como "H2O - óxido de dihidrógeno" o "metano"
        String direccion; // Como "www.fq.com/H2O"
    }

    public InorganicoResultado buscar(String input, Boolean usuario_premium) { // En construcción
        InorganicoResultado resultado;

        Integer id = buscarDB(input); // Flowchart #0

        // Flowchart #1
        if(id == null) { // No se encuentra en la DB
            BusquedaWeb busqueda_web = null;

            // Flowchart #2
            if(configuracionService.getGoogleON() /*&& limite */)
                busqueda_web = tryBuscarGoogle(input);

            // Flowchart #3
            if(busqueda_web == null && configuracionService.getBingGratisON())
                busqueda_web = tryBuscarBing(input, configuracionService.getBingGratisKey());

            // Flowchart #4
            if(busqueda_web == null && configuracionService.getBingPagoON() /*&& limite */)
                busqueda_web = tryBuscarBing(input, configuracionService.getBingPagoKey());

            // Flowchart #0 ó #5
            if(busqueda_web != null && busqueda_web.encontrado) { // Se ha podido encontrar con Google o Bing
                String primera_palabra = busqueda_web.titulo.trim().split(" ", 2)[0];
                id = buscarDB(primera_palabra); // Flowchart #0

                // Flowchart #5
                if(id == null) // No estaba en la DB, es nuevo
                    resultado = escanearFQ(busqueda_web.direccion);
                // Flowchart #6
                else { // Ya estaba en la DB
                    resultado = decidirPremium(id, usuario_premium);
                    registrarBusqueda(id);
                }
            }
            // Flowchart #7
            else { // No se ha podido encontrar ni con Google ni con Bing
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
    private BusquedaWeb tryBuscarGoogle(String input) {
        BusquedaWeb busqueda_web;

        try {
            busqueda_web = buscarGoogle(input);
        }
        catch (Exception e) {
            busqueda_web = null;
        }

        return busqueda_web;
    }

    // Flowchart #2
    private BusquedaWeb buscarGoogle(String input) throws Exception {
        BusquedaWeb busqueda_web = new BusquedaWeb();

        HttpURLConnection conexion = (HttpURLConnection) new URL(
                configuracionService.getGoogleURL() + formatearHTTP(input)).openConnection();
        conexion.setRequestMethod("GET");
        conexion.setRequestProperty("Accept", "application/json");

        JSONObject respuesta = new JSONObject(descargarTexto(conexion));

        if(respuesta.getJSONObject("searchInformation").getInt("totalResults") > 0) {
            JSONObject resultado = respuesta.getJSONArray("items").getJSONObject(0);

            busqueda_web.encontrado = true;
            busqueda_web.titulo = resultado.getString("title");
            busqueda_web.direccion = resultado.getString("formattedUrl"); // "www.fq.com/..."
        }
        else busqueda_web.encontrado = false;

        return busqueda_web;
    }

    // Flowchart #3 ó #4
    private BusquedaWeb tryBuscarBing(String input, String key) {
        BusquedaWeb busqueda_web;

        try {
            busqueda_web = buscarBing(input, key);
        }
        catch (Exception e) {
            busqueda_web = null;
        }

        return busqueda_web;
    }

    // Flowchart #3 ó #4
    private BusquedaWeb buscarBing(String input, String key) throws Exception {
        BusquedaWeb busqueda_web = new BusquedaWeb();

        HttpURLConnection conexion = (HttpURLConnection) new URL(
                configuracionService.getBingURL() + formatearHTTP(input)).openConnection();
        conexion.setRequestProperty("Ocp-Apim-Subscription-Key", key);

        JSONObject respuesta = new JSONObject(descargarTexto(conexion));

        if(respuesta.has("webPages")) {
            JSONObject resultado = respuesta.getJSONObject("webPages").getJSONArray("value").getJSONObject(0);

            busqueda_web.encontrado = true;
            busqueda_web.titulo = resultado.getString("name");
            busqueda_web.direccion = resultado.getString("url"); // "www.fq.com/..."
        }
        else busqueda_web.encontrado = false;

        return busqueda_web;
    }

    // Flowchart #2 ó #3 ó #4
    private String formatearHTTP(String input) {
        return URLEncoder.encode(input, StandardCharsets.UTF_8);
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
        BufferedReader descarga = new BufferedReader(new InputStreamReader(conexion.getInputStream()));

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
        InorganicoModel resultado = inorganicoRepository.findById(id).get();

        return (!resultado.getPremium() || usuario_premium)
                ? new InorganicoResultado(resultado) : NO_PREMIUM;
    }

}
