package com.quimify.servidor.inorganico;

import com.quimify.servidor.Normalizar;
import com.quimify.servidor.conexion.Conexion;
import com.quimify.servidor.configuracion.ConfiguracionService;
import com.quimify.servidor.metricas.MetricasService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Esta clase procesa los compuestos inorgánicos.

@Service
public class InorganicoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    InorganicoRepository inorganicoRepository; // Conexión con la DB

    @Autowired
    ConfiguracionService configuracionService; // Procesos de la configuración

    @Autowired
    MetricasService metricaService; // Procesos de las metricas diarias

    private static final List<InorganicoBuscable> BUSCABLES = new ArrayList<>(); // Para ser buscados rápidamente
    public static final InorganicoResultado NO_ENCONTRADO = new InorganicoResultado(); // Constante auxiliar

    // RUTINAS -----------------------------------------------------------------------

    public void cargarInorganicosBuscables() {
        for(InorganicoModel inorganico : inorganicoRepository.findAllByOrderByBusquedasDesc())
            BUSCABLES.add(new InorganicoBuscable(inorganico));
    }

    // CLIENTE -----------------------------------------------------------------------

    public InorganicoResultado buscar(String input, Boolean foto) {
        InorganicoResultado resultado;

        Optional<Integer> id = buscarMemoriaPrincipal(input); // Flowchart #0

        // Flowchart #1
        if(id.isEmpty()) { // No se encuentra en la DB
            Optional<BusquedaWeb> busqueda_web;

            // Flowchart #2
            if(disponibleGoogle()) {
                busqueda_web = tryBuscarGoogle(input);

                metricaService.contarGoogle(busqueda_web.isPresent() && busqueda_web.get().encontrado, foto);
            }
            // Flowchart #3
            else if(disponibleBingGratis()) {
                busqueda_web = tryBuscarBingGratis(input);

                metricaService.contarBing(busqueda_web.isPresent() && busqueda_web.get().encontrado, foto);
            }
            // Flowchart #4
            else if(disponibleBingPago()) {
                busqueda_web = tryBuscarBingPago(input);

                metricaService.contarBingPago();
                metricaService.contarBing(busqueda_web.isPresent() && busqueda_web.get().encontrado, foto);
            }
            // Flowchart #7
            else busqueda_web = Optional.empty();

            // Flowchart #0 ó #5
            if(busqueda_web.isPresent() && busqueda_web.get().encontrado) { // Se ha podido encontrar con Google o Bing
                String[] palabras = busqueda_web.get().titulo.trim().split(" ");

                String primera_palabra = palabras[0];
                if(primera_palabra.equals("ácido"))
                    primera_palabra += palabras[1];

                // Flowchart #0
                id = buscarMemoriaPrincipal(primera_palabra); // Suele ser la fórmula

                // Flowchart #5
                if(id.isEmpty()) { // Parece no estar en la DB
                    Optional<InorganicoModel> escaneado = escanearFQ(busqueda_web.get().direccion);

                    if(escaneado.isPresent()) { // Escaneado correctamente
                        id = buscarMemoriaPrincipal(escaneado.get().getNombre());

                        if(id.isEmpty()) { // En efecto, no estaba en la DB
                            resultado = new InorganicoResultado(escaneado.get());
                            guardarNuevoEscaneado(escaneado.get());
                            metricaService.contarInorganicoNuevo();
                        }
                        else { // Realmente sí estaba en la DB
                            resultado = buscarDB(id.get());

                            logger.warn("El inorgánico buscado en la web \"" + input + "\", una vez escaneado, era " +
                                    "id = " + id.get() + ".");
                        }
                    }
                    else resultado = NO_ENCONTRADO;
                }
                // Flowchart #6
                else { // Ya estaba en la DB
                    resultado = buscarDB(id.get());

                    logger.warn("El inorgánico buscado en la web \"" + input + "\" era id = " + id.get() + ".");
                }
            }
            // Flowchart #7
            else { // No se ha podido encontrar ni con Google ni con Bing
                resultado = NO_ENCONTRADO; // Temporal
                // Sugerencia...

                logger.warn("No se ha encontrado el inorgánico: " + input);
            }
        }
        // Flowchart #6
        else resultado = buscarDB(id.get()); // Está en la DB

        metricaService.contarInorganicoBuscado(resultado.getEncontrado(), foto);

        return resultado;
    }

    public String autoCompletar(String input) {
        String resultado = "";

        input = new Normalizar(input).get(); // Para poder hacer la búsqueda
        for(InorganicoBuscable buscable : BUSCABLES) { // Ordenados por nº de búsquedas
            String complecion = buscable.autoCompletar(input); // Devuelve un keyword solo si puede autocompletar

            if(complecion != null) { // Encontrado inorgánico que completa
                Optional<InorganicoModel> encontrado = inorganicoRepository.findById(buscable.getId());

                if(encontrado.isPresent()) {
                    if(complecion.equals(new Normalizar(encontrado.get().getFormula()).get()))
                        resultado = encontrado.get().getFormula(); // Fórmula puede autocompletar
                    else if(complecion.equals(new Normalizar(encontrado.get().getAlternativo()).get()))
                        resultado = encontrado.get().getAlternativo(); // Alternativo puede autocompletar
                    else resultado = encontrado.get().getNombre(); // El nombre (o una etiqueta)

                    break;
                }
                else logger.error("El compuesto en memoria id = " + buscable.getId() + " no está en la DB.");
            }
        }

        return resultado;
    }

    public InorganicoResultado buscarComplecion(String complecion) {
        InorganicoResultado resultado;

        Optional<Integer> id = buscarMemoriaPrincipal(complecion);
        if(id.isPresent())
            resultado = buscarDB(id.get());
        else {
            logger.error("La compleción: \"" + complecion + "\" no se encuentra.");
            resultado = NO_ENCONTRADO;
        }

        metricaService.contarInorganicoAutocompletado();
        metricaService.contarInorganicoBuscado(resultado.getEncontrado(), false);

        return resultado;
    }

    // INTERNOS ----------------------------------------------------------------------

    // Flowchart #0
    private Optional<Integer> buscarMemoriaPrincipal(String input) {
        input = new Normalizar(input).get();

        for(InorganicoBuscable buscable : BUSCABLES) // Ordenados por nº de búsquedas
            if(buscable.coincide(input))
                return Optional.of(buscable.getId());

        return Optional.empty();
    }

    private Boolean disponibleGoogle() {
        boolean superadas = metricaService.getBusquedasGoogle() >= configuracionService.getGoogleLimite();

        if(superadas && configuracionService.getGoogleON())
            logger.warn("Búsquedas de Google superadas");

        return !superadas && configuracionService.getGoogleON();
    }

    private Boolean disponibleBingGratis() {
        // Cuando devuelve 403 se avisa de que se han superado las de ese mes
        return configuracionService.getBingGratisON();
    }

    private Boolean disponibleBingPago() {
        boolean superadas = metricaService.getBusquedasBingPago() >= configuracionService.getBingPagoLimite();

        if(superadas && configuracionService.getBingPagoON())
            logger.warn("Búsquedas de Bing de pago superadas");

        return !superadas && configuracionService.getBingPagoON();
    }

    private static class BusquedaWeb {
        // Resultado de una búsqueda web (de cualquier buscador)
        boolean encontrado; // Se ha encontrado algo
        String titulo; // Como "H2O - óxido de dihidrógeno" o "metano"
        String direccion; // Como "www.fq.com/H2O"
    }

    // Flowchart #2
    private Optional<BusquedaWeb> tryBuscarGoogle(String input) {
        Optional<BusquedaWeb> busqueda_web;

        try {
            busqueda_web = Optional.of(buscarGoogle(input));
        }
        catch (Exception exception) {
            busqueda_web = Optional.empty();

            logger.error("IOException al buscar \"" + input + "\" en Google: " + exception);
        }

        return busqueda_web;
    }

    // Flowchart #2
    private BusquedaWeb buscarGoogle(String input) throws Exception {
        BusquedaWeb busqueda_web = new BusquedaWeb();

        Conexion conexion = new Conexion(configuracionService.getGoogleURL(), input);
        conexion.setPropiedad("Accept", "application/json");
        JSONObject respuesta = new JSONObject(conexion.getTexto());

        if(respuesta.getJSONObject("searchInformation").getInt("totalResults") > 0) {
            JSONObject resultado = respuesta.getJSONArray("items").getJSONObject(0);

            busqueda_web.encontrado = true;
            busqueda_web.titulo = resultado.getString("title");
            busqueda_web.direccion = resultado.getString("formattedUrl"); // "www.fq.com/..."
        }
        else {
            busqueda_web.encontrado = false;

            logger.warn("No se ha encontrado \"" + input + "\" en Google.");
        }

        return busqueda_web;
    }

    // Flowchart #3
    private Optional<BusquedaWeb> tryBuscarBingGratis(String input) {
        Optional<BusquedaWeb> busqueda_web;

        try {
            busqueda_web = Optional.of(buscarBing(input, configuracionService.getBingGratisKey()));
        }
        catch(IOException exception) {
            busqueda_web = Optional.empty();

            if(exception.toString().contains("HTTP response code: 403"))
                logger.warn("Bing gratis ha devuelto HTTP 403.");
            else logger.error("IOException al buscar \"" + input + "\" en Bing: " + exception);
        }
        catch (Exception exception) {
            busqueda_web = Optional.empty();

            logger.error("Exception al buscar \"" + input + "\" en Bing: " + exception);
        }

        return busqueda_web;
    }

    // Flowchart #4
    private Optional<BusquedaWeb> tryBuscarBingPago(String input) {
        Optional<BusquedaWeb> busqueda_web;

        try {
            busqueda_web = Optional.of(buscarBing(input, configuracionService.getBingPagoKey()));
        }
        catch(IOException exception) {
            busqueda_web = Optional.empty();

            if(exception.toString().contains("HTTP response code: 403"))
                logger.warn("Bing de pago ha devuelto HTTP 403.");
            else logger.error("IOException al buscar \"" + input + "\" en Bing de pago: " + exception);
        }
        catch (Exception exception) {
            busqueda_web = Optional.empty();

            logger.error("Exception al buscar \"" + input + "\" en Bing de pago: " + exception);
        }

        return busqueda_web;
    }

    // Flowchart #3 ó #4
    private BusquedaWeb buscarBing(String input, String key) throws Exception {
        BusquedaWeb busqueda_web = new BusquedaWeb();

        Conexion conexion = new Conexion(configuracionService.getBingURL(),input);
        conexion.setPropiedad("Ocp-Apim-Subscription-Key", key);
        JSONObject respuesta = new JSONObject(conexion.getTexto());

        if(respuesta.has("webPages")) {
            JSONObject resultado = respuesta.getJSONObject("webPages")
                    .getJSONArray("value").getJSONObject(0);

            busqueda_web.encontrado = true;
            busqueda_web.titulo = resultado.getString("name");
            busqueda_web.direccion = resultado.getString("url"); // Será "www.fq.com/..."
        }
        else {
            busqueda_web.encontrado = false;

            logger.warn("No se ha encontrado \"" + input + "\" en Bing.");
        }

        return busqueda_web;
    }

    // Flowchart #5
    private Optional<InorganicoModel> escanearFQ(String direccion) {
        Optional<InorganicoModel> resultado;

        try {
            Conexion conexion = new Conexion(direccion);
            conexion.setPropiedad("User-Agent", configuracionService.getUserAgent());

            PaginaFQ pagina_fq = new PaginaFQ(conexion.getTexto());
            resultado = pagina_fq.escanearInorganico();
        }
        catch (Exception exception) {
            resultado = Optional.empty();

            logger.error("Excepción al escanear la dirección \"" + direccion + "\": " + exception);
        }

        return resultado;
    }

    private void guardarNuevoEscaneado(InorganicoModel nuevo) {
        BUSCABLES.add(new InorganicoBuscable( // En memoria principal para ser buscado
                inorganicoRepository.save(nuevo))); // En la DB
    }

    // Incrementa el contador de búsquedas de un inorgánico porque ha sido buscado
    private void nuevaBusquedaInorganico(Integer id) {
        Optional<InorganicoModel> buscado = inorganicoRepository.findById(id);

        if(buscado.isPresent()) {
            buscado.get().registrarBusqueda();
            inorganicoRepository.save(buscado.get());
        }
        else logger.error("El inorgánico id = " + id + " ha desaparecido de la DB.");
    }

    // Flowchart #6
    private InorganicoResultado buscarDB(Integer id) {
        InorganicoResultado resultado;

        Optional<InorganicoModel> encontrado = inorganicoRepository.findById(id);
        if(encontrado.isPresent()) {
            resultado = new InorganicoResultado(encontrado.get());
            nuevaBusquedaInorganico(id);
        }
        else { // 'BUSCABLES' discrepa con la DB
            resultado = NO_ENCONTRADO;

            logger.error("El inorgánico en memoria id = " + id + " no está en la DB.");
        }

        return resultado;
    }

    // ADMIN --------------------------------------------------------------------------

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

}
