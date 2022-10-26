package com.quimify.api.inorganico;

import com.quimify.api.Normalizado;
import com.quimify.api.masa_molecular.MasaMolecularResultado;
import com.quimify.api.masa_molecular.MasaMolecularService;
import com.quimify.api.configuracion.ConfiguracionService;
import com.quimify.api.metricas.MetricasService;
import com.quimify.utils.Download;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Esta clase procesa los compuestos inorgánicos.

@Service
public class InorganicoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    InorganicoRepository inorganicoRepository; // Conexión con la DB

    @Autowired
    MasaMolecularService masaMolecularService; // Procesos de la masa molecular

    @Autowired
    ConfiguracionService configuracionService; // Procesos de la configuración

    @Autowired
    MetricasService metricasService; // Procesos de las metricas diarias

    public static final InorganicoResultado NO_ENCONTRADO = new InorganicoResultado(); // Constante auxiliar

    // AUTOCOMPLECIÓN ----------------------------------------------------------------

    private static List<InorganicoNormalizado> NORMALIZADOS = new ArrayList<>();

    public void cargarNormalizados() {
        NORMALIZADOS = inorganicoRepository.findAllByOrderByBusquedasDesc()
                .stream().map(InorganicoNormalizado::new).collect(Collectors.toList());

        logger.info("Normalizados cargados");
    }

    public String autoComplete(String input) {
        String resultado = "";

        input = Normalizado.of(input); // Para poder hacer la búsqueda

        for (InorganicoNormalizado normalizado : NORMALIZADOS) {
            if (normalizado.completaFormula(input))
                return normalizado.getFormulaOriginal(); // Fórmula puede autocompletar
            if (normalizado.completaAlternativo(input))
                return normalizado.getAlternativoOriginal(); // Alternativo puede autocompletar
            if (normalizado.completaNombre(input)
                    || normalizado.completanEtiquetas(input))
                return normalizado.getNombreOriginal(); // Nombre o una etiqueta puede autocompletar
        }

        return resultado;
    }

    // BÚSQUEDAS ---------------------------------------------------------------------

    public InorganicoResultado searchFromCompletion(String completion) {
        InorganicoResultado resultado;

        Optional<InorganicoModel> buscado = searchInMemory(completion);
        if (buscado.isPresent())
            resultado = new InorganicoResultado(buscado.get());
        else {
            logger.error("La compleción: \"" + completion + "\" no se encuentra.");
            resultado = NO_ENCONTRADO;
        }

        metricasService.contarInorganicoAutocompletado();
        metricasService.contarInorganicoBuscado(resultado.getEncontrado(), false);

        return resultado;
    }

    public InorganicoResultado search(String input, Boolean isPicture) {
        InorganicoResultado inorganicResult;

        Optional<InorganicoModel> searchedInMemory = searchInMemory(input); // Flowchart #0

        // Flowchart #1
        if (searchedInMemory.isEmpty()) { // No se encuentra en la DB
            Optional<SearchResult> searchResult = Optional.empty();

            // Flowchart #2
            if (canGoogleSearch()) {
                searchResult = tryGoogleSeach(input);

                metricasService.contarGoogle(searchResult.isPresent() && searchResult.get().found, isPicture);
            }
            // Flowchart #3
            if (searchResult.isEmpty() && canFreeBingSearch()) { // Hubo con Google o no está disponible
                searchResult = tryFreeBingSearch(input);

                metricasService.contarBing(searchResult.isPresent() && searchResult.get().found, isPicture);
            }
            // Flowchart #4
            if (searchResult.isEmpty() && canPaidBingSearch()) { // Hubo con Bing gratis o no está disponible
                searchResult = tryPaidBingSearch(input);

                metricasService.contarBingPago();
                metricasService.contarBing(searchResult.isPresent() && searchResult.get().found, isPicture);
            }

            // Flowchart #0 ó #5
            if (searchResult.isPresent() && searchResult.get().found) { // Se ha podido encontrar con Google o Bing
                String[] words = searchResult.get().title.trim().split(" ");

                String firstWord = words[0];
                if (firstWord.equals("ácido"))
                    firstWord += words[1];

                // Flowchart #0
                searchedInMemory = searchInMemory(firstWord); // Suele ser la fórmula

                // Flowchart #5
                if (searchedInMemory.isEmpty()) { // Parece no estar en la DB
                    Optional<InorganicoModel> parsed = tryParseFQ(searchResult.get().address);

                    if (parsed.isPresent()) { // Escaneado correctamente
                        searchedInMemory = searchInMemory(parsed.get().getNombre());

                        if (searchedInMemory.isEmpty()) { // En efecto, no estaba en la DB
                            if (parsed.get().getMasa() == null) {
                                MasaMolecularResultado masaMolecularResultado =
                                        masaMolecularService.tryMasaMolecularDe(parsed.get().getFormula());

                                if (masaMolecularResultado.getEncontrado())
                                    parsed.get().setMasa(masaMolecularResultado.getMasa().toString());
                            }

                            inorganicResult = new InorganicoResultado(parsed.get());

                            inorganicoRepository.save(parsed.get());

                            logger.info("Nuevo inorgánico: " + parsed.get());
                            metricasService.contarInorganicoNuevo();
                        } else { // Realmente sí estaba en la DB
                            inorganicResult = new InorganicoResultado(searchedInMemory.get());

                            logger.warn("El inorgánico parseado \"" + input + "\" era: " + searchedInMemory.get().getId());
                        }
                    } else inorganicResult = NO_ENCONTRADO;
                }
                // Flowchart #6
                else { // Ya estaba en la DB
                    inorganicResult = new InorganicoResultado(searchedInMemory.get());
                    logger.warn("El inorgánico buscado en la web \"" + input + "\" era: " + searchedInMemory.get());
                }
            }
            // Flowchart #7
            else { // No se ha podido encontrar ni con Google ni con Bing
                inorganicResult = NO_ENCONTRADO; // Temporal
                // Sugerencia... TODO

                logger.warn("No se ha encontrado el inorgánico \"" + input + "\".");
            }
        }
        // Flowchart #6
        else {
            inorganicResult = new InorganicoResultado(searchedInMemory.get()); // Está en la DB
        }

        metricasService.contarInorganicoBuscado(inorganicResult.getEncontrado(), isPicture);

        return inorganicResult;
    }

    // INTERNOS ----------------------------------------------------------------------

    // Flowchart #0
    private Optional<InorganicoModel> searchInMemory(String input) {
        input = Normalizado.of(input);

        for (InorganicoModel inorganico : inorganicoRepository.findAllByOrderByBusquedasDesc())
            if (input.equals(Normalizado.of(inorganico.getFormula()))
                    || input.equals(Normalizado.of(inorganico.getNombre()))
                    || input.equals(Normalizado.of(inorganico.getAlternativo()))
                    || inorganico.getEtiquetasString().contains(input)) {
                nuevaBusqueda(inorganico);
                return Optional.of(inorganico);
            }

        return Optional.empty();
    }

    private Boolean canGoogleSearch() {
        if(!configuracionService.getGoogleON())
            return false;

        int busquedas = metricasService.getBusquedasGoogle();

        if (busquedas == configuracionService.getBingPagoLimite())
            logger.warn("Búsquedas de Google superadas.");

        return busquedas < configuracionService.getGoogleLimite();
    }

    private Boolean canFreeBingSearch() {
        // Cuando devuelve 403 se avisa de que se han superado las de ese mes
        return configuracionService.getBingGratisON();
    }

    private Boolean canPaidBingSearch() {
        if(!configuracionService.getBingPagoON())
            return false;

        int busquedas = metricasService.getBusquedasBingPago();

        if (busquedas == configuracionService.getBingPagoLimite())
            logger.warn("Búsquedas de Bing de pago superadas.");

        return busquedas < configuracionService.getBingPagoLimite();
    }

    private static class SearchResult {
        // Resultado de una búsqueda web (de cualquier buscador)
        boolean found; // Se ha encontrado algo
        String title; // Como "H2O - óxido de dihidrógeno" o "metano"
        String address; // Como "www.fq.com/H2O"
    }

    // Flowchart #2
    private Optional<SearchResult> tryGoogleSeach(String input) {
        Optional<SearchResult> busqueda_web;

        try {
            busqueda_web = Optional.of(googleSearch(input));
        } catch (Exception exception) {
            busqueda_web = Optional.empty();

            if (exception.toString().contains("Server returned HTTP response code: 429"))
                logger.warn("Google ha devuelto HTTP 429.");
            else logger.error("IOException al buscar \"" + input + "\" en Google: " + exception);
        }

        return busqueda_web;
    }

    // Flowchart #2
    private SearchResult googleSearch(String input) throws Exception {
        SearchResult busqueda_web = new SearchResult();

        Download conexion = new Download(configuracionService.getGoogleURL(), input);
        conexion.setProperty("Accept", "application/json");
        JSONObject respuesta = new JSONObject(conexion.getText());

        if (respuesta.getJSONObject("searchInformation").getInt("totalResults") > 0) {
            JSONObject resultado = respuesta.getJSONArray("items").getJSONObject(0);

            busqueda_web.found = true;
            busqueda_web.title = resultado.getString("title");
            busqueda_web.address = resultado.getString("formattedUrl"); // "www.fq.com/..."
        } else {
            busqueda_web.found = false;

            logger.warn("No se ha encontrado \"" + input + "\" en Google.");
        }

        return busqueda_web;
    }
    
    // Flowchart #3
    private Optional<SearchResult> tryFreeBingSearch(String input) {
        return tryBingSearch(input, configuracionService.getBingGratisKey(), "gratis");
    }
    
    // Flowchart #4
    private Optional<SearchResult> tryPaidBingSearch(String input) {
        return tryBingSearch(input, configuracionService.getBingPagoKey(), "pago");
    }
    
    // Flowchart #3, #4
    private Optional<SearchResult> tryBingSearch(String input, String apiKey, String apiName) {
        Optional<SearchResult> searchResult;

        try {
            searchResult = Optional.of(bingSearch(input, apiKey));
        } catch (IOException exception) {
            searchResult = Optional.empty();

            if (exception.toString().contains("HTTP response code: 403"))
                logger.warn("Bing " + apiName + " ha devuelto HTTP 403.");
            else logger.error("IOException al buscar \"" + input + "\" en Bing " + apiName + ": " + exception);
        } catch (Exception exception) {
            searchResult = Optional.empty();

            logger.error("Exception al buscar \"" + input + "\" en Bing " + apiName + ": " + exception);
        }

        return searchResult;
    }

    // Flowchart #3 ó #4
    private SearchResult bingSearch(String input, String key) throws Exception {
        SearchResult busqueda_web = new SearchResult();

        Download conexion = new Download(configuracionService.getBingURL(), input);
        conexion.setProperty("Ocp-Apim-Subscription-Key", key);
        JSONObject respuesta = new JSONObject(conexion.getText());

        if (respuesta.has("webPages")) {
            JSONObject resultado = respuesta.getJSONObject("webPages")
                    .getJSONArray("value").getJSONObject(0);

            busqueda_web.found = true;
            busqueda_web.title = resultado.getString("name");
            busqueda_web.address = resultado.getString("url"); // Será "www.fq.com/..."
        } else {
            busqueda_web.found = false;

            logger.warn("No se ha encontrado \"" + input + "\" en Bing.");
        }

        return busqueda_web;
    }

    // Flowchart #5
    private Optional<InorganicoModel> tryParseFQ(String address) {
        // FQ subdirectories that are NOT compounds:
        if(address.matches("^.*?(acidos-carboxilicos|alcanos|alcoholes|aldehidos|alquenos|alquinos|amidas|" +
                "aminas|anhidridos|anhidridos-organicos|aromaticos|buscador|cetonas|cicloalquenos|ejemplos|" +
                "ejercicios|eteres|halogenuros|hidracidos|hidroxidos|hidruros|hidruros-volatiles|inorganica|" +
                "nitrilos|organica|oxidos|oxisales|oxoacidos|peroxidos|politica-privacidad|sales-neutras|" +
                "sales-volatiles).*$") || address.matches("^.*?(.com)/?$"))
            return Optional.empty();

        Optional<InorganicoModel> resultado;

        try {
            Download conexion = new Download(address);
            conexion.setProperty("User-Agent", configuracionService.getUserAgent());

            FormulacionQuimicaPage pagina_fq = new FormulacionQuimicaPage(conexion.getText());
            resultado = pagina_fq.escanearInorganico();

            if(resultado.isEmpty())
                logger.error("No se pudo escanear la dirección \"" + address + "\".");
        } catch (Exception exception) {
            resultado = Optional.empty();

            logger.error("Excepción al escanear la dirección \"" + address + "\": " + exception);
        }

        return resultado;
    }

    // Incrementa el contador de búsquedas de un inorgánico porque ha sido buscado
    private void nuevaBusqueda(InorganicoModel buscado) {
        buscado.registrarBusqueda();
        inorganicoRepository.save(buscado);
    }

}
