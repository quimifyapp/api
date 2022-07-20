package com.quimify.api.inorganico;

import com.quimify.api.Normalizar;
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
import java.util.List;
import java.util.Optional;

// Esta clase procesa los compuestos inorgánicos.

@Service
public class InorganicoService {

    @Autowired
    private InorganicoRepository inorganicoRepository; // Conexión con la DB

    @Autowired
    ConfiguracionService configuracionService; // Procesos de la configuración

    private static final List<InorganicoBuscable> BUSCABLES = new ArrayList<>(); // Para ser buscados rápidamente

    public final static InorganicoResultado NO_ENCONTRADO = new InorganicoResultado(InorganicoResultado.NO_ENCONTRADO);
    public final static Integer ENCONTRADO = InorganicoResultado.ENCONTRADO; // OK
    public final static Integer SUGERENCIA = InorganicoResultado.SUGERENCIA; // Quizás quisiste decir...

    // ADMIN --------------------------------------------------------------------------

    // TODO: quitar los 'usuario_premium'?

    public Optional<InorganicoModel> seleccionar(Integer id) {
        return inorganicoRepository.findById(id);
    }

    private void reemplazarExistente(InorganicoModel existente) {
        inorganicoRepository.save(existente); // En la DB

        for(int i = 0; i < BUSCABLES.size(); i++) // En memoria principal para ser buscado
            if(BUSCABLES.get(i).getId().equals(existente.getId())) {
                BUSCABLES.set(i, new InorganicoBuscable(existente));
                break;
            }
    }

    public Optional<InorganicoModel> reemplazar(InorganicoModel nuevo) {
        Optional<InorganicoModel> reemplazado = inorganicoRepository.findById(nuevo.getId());

        if(reemplazado.isPresent()) // Si existe
            reemplazarExistente(nuevo);

        return reemplazado;
    }

    public Optional<InorganicoModel> hacerPremium(Integer id) {
        Optional<InorganicoModel> reemplazado = inorganicoRepository.findById(id);

        if(reemplazado.isPresent()) { // Si existe
            reemplazado.get().setPremium(true);
            reemplazarExistente(reemplazado.get());
        }

        return reemplazado;
    }

    public InorganicoModel insertar(InorganicoModel nuevo) {
        InorganicoModel insertado;

        insertado = inorganicoRepository.save(nuevo); // En la DB
        BUSCABLES.add(new InorganicoBuscable(insertado)); // En memoria principal

        return insertado;
    }

    public Optional<InorganicoModel> eliminar(Integer id) {
        Optional<InorganicoModel> eliminado = inorganicoRepository.findById(id);

        if(eliminado.isPresent()) {
            inorganicoRepository.deleteById(id); // De la DB
            BUSCABLES.removeIf(i -> id.equals(i.getId())); // De la memoria principal
        }

        return eliminado;
    }

    public Optional<InorganicoModel> probarPaginaFQ(String direccion) { // TEST
        Optional<InorganicoModel> resultado;

        try {
            HttpURLConnection conexion = (HttpURLConnection) new URL(direccion).openConnection();
            conexion.setRequestProperty("User-Agent", configuracionService.getUserAgent());

            PaginaFQ pagina = new PaginaFQ(descargarTexto(conexion));
            InorganicoModel inorganico = pagina.escanearInorganico();

            resultado = Optional.of(inorganico);
        } catch (Exception e) {
            resultado = Optional.empty();
        }

        return resultado;
    }

    // RUTINAS -----------------------------------------------------------------------

    public void cargarBuscables() {
        for(InorganicoModel inorganico : inorganicoRepository.findAllByOrderByBusquedasDesc())
            BUSCABLES.add(new InorganicoBuscable(inorganico));
    }

    // INTERNOS ----------------------------------------------------------------------

    private void guardarNuevo(InorganicoModel nuevo) {
        BUSCABLES.add(new InorganicoBuscable( // En memoria principal para ser buscado
                inorganicoRepository.save(nuevo))); // En la DB
    }

    // Incrementa el contador de búsquedas de ese inorgánico porque ha sido buscado
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

        input = new Normalizar(input).get(); // Para poder hacer la búsqueda
        for(InorganicoBuscable buscable : BUSCABLES) { // Ordenados por nº de búsquedas
            String complecion = buscable.autoCompletar(input); // Devuelve un keyword solo si puede autocompletar

            if(complecion != null) { // Encontrado inorgánico que completa
                Optional<InorganicoModel> encontrado = inorganicoRepository.findById(buscable.getId());

                if(encontrado.isPresent()) {
                    if(complecion.equals(new Normalizar(encontrado.get().getFormula()).get()))
                        resultado = encontrado.get().getFormula(); // La fórmula puede autocompletar
                    else if(complecion.equals(new Normalizar(encontrado.get().getAlternativo()).get()))
                        resultado = encontrado.get().getAlternativo(); // El alternativo puede autocompletar
                    else resultado = encontrado.get().getNombre(); // El nombre o una etiqueta pueden autocompletar

                    break;
                }
                else {
                    // Error...
                }
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

        Integer id = buscarMemoriaPrincipal(input); // Flowchart #0

        // Flowchart #1
        if(id == null) { // No se encuentra en la DB
            BusquedaWeb busqueda_web;

            // Flowchart #2
            if(configuracionService.getGoogleON() /*&& limite */)
                busqueda_web = tryBuscarGoogle(input);
            else busqueda_web = null;

            // Flowchart #3
            if(busqueda_web == null && configuracionService.getBingGratisON())
                busqueda_web = tryBuscarBing(input, configuracionService.getBingGratisKey());

            // Flowchart #4
            if(busqueda_web == null && configuracionService.getBingPagoON() /*&& limite */)
                busqueda_web = tryBuscarBing(input, configuracionService.getBingPagoKey());

            // Flowchart #0 ó #5
            if(busqueda_web != null && busqueda_web.encontrado) { // Se ha podido encontrar con Google o Bing
                String primera_palabra = busqueda_web.titulo.trim().split(" ", 2)[0];
                id = buscarMemoriaPrincipal(primera_palabra); // Flowchart #0

                // Flowchart #5
                if(id == null) // Parece no estar en la DB
                    resultado = escanearFQ(busqueda_web.direccion);
                // Flowchart #6
                else resultado = buscarDB(id); // Ya estaba en la DB
            }
            // Flowchart #7
            else { // No se ha podido encontrar ni con Google ni con Bing
                resultado = NO_ENCONTRADO; // Test
                // ...
            }
        }
        // Flowchart #6
        else resultado = buscarDB(id); // Está en la DB

        return resultado;
    }

    // Flowchart #0
    private Integer buscarMemoriaPrincipal(String input) {
        input = new Normalizar(input).get();

        for(InorganicoBuscable buscable : BUSCABLES) // Ordenados por nº de búsquedas
            if(buscable.coincide(input))
                return buscable.getId();

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
            // ...
        }

        return busqueda_web;
    }

    // Flowchart #2
    private BusquedaWeb buscarGoogle(String input) throws Exception {
        BusquedaWeb busqueda_web = new BusquedaWeb();

        String url = configuracionService.getGoogleURL() + formatearHTTP(input);
        HttpURLConnection conexion = (HttpURLConnection) new URL(url).openConnection();
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
            // ...
        }

        return busqueda_web;
    }

    // Flowchart #3 ó #4
    private BusquedaWeb buscarBing(String input, String key) throws Exception {
        BusquedaWeb busqueda_web = new BusquedaWeb();

        String url = configuracionService.getBingURL() + formatearHTTP(input);
        HttpURLConnection conexion = (HttpURLConnection) new URL(url).openConnection();
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

                resultado = new InorganicoResultado(escaneado, InorganicoResultado.ENCONTRADO);
            }
            else {
                resultado = NO_ENCONTRADO;
                // ...
            }
        }
        catch (Exception e) {
            resultado = NO_ENCONTRADO;
            // ...
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

    // Flowchart #6
    private InorganicoResultado buscarDB(Integer id) {
        InorganicoResultado resultado;

        Optional<InorganicoModel> encontrado = inorganicoRepository.findById(id);
        if(encontrado.isPresent()) { // Por si 'BUSCABLES' discrepa con la DB
            resultado = new InorganicoResultado(encontrado.get(), ENCONTRADO);
            registrarBusqueda(id);
        }
        else {
            resultado = NO_ENCONTRADO;
            // ...
        }

        return resultado;
    }

}
