package com.quimify.api.inorganic;

import com.quimify.api.Normalized;
import com.quimify.api.molecular_mass.MolecularMassService;
import com.quimify.api.settings.SettingsService;
import com.quimify.api.metrics.MetricsService;
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
public
class InorganicService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    InorganicRepository inorganicRepository; // Conexión con la DB

    @Autowired
    MolecularMassService molecularMassService; // Procesos de la masa molecular

    @Autowired
    SettingsService settingsService; // Procesos de la configuración

    @Autowired
    MetricsService metricsService; // Procesos de las metricas diarias

    protected static final InorganicResult notFoundInorganic = new InorganicResult(); // Constante auxiliar

    // AUTOCOMPLECIÓN ----------------------------------------------------------------

    private static List<NormalizedInorganic> normalizedInorganics = new ArrayList<>();

    public void loadNormalizedInorganics() {
        normalizedInorganics = inorganicRepository.findAllByOrderBySearchCountDesc()
                .stream().map(NormalizedInorganic::new).collect(Collectors.toList());

        logger.info("Normalizados cargados");
    }

    protected String autoComplete(String input) {
        String completion = "";

        input = Normalized.of(input); // Para poder hacer la búsqueda

        if (!input.equals("")) {
            for (NormalizedInorganic normalizedInorganic : normalizedInorganics) {
                if (normalizedInorganic.formulaCanComplete(input))
                    return normalizedInorganic.getOriginalFormula(); // Fórmula puede autocompletar
                if (normalizedInorganic.alternativeNameCanComplete(input))
                    return normalizedInorganic.getOriginalAlternativeName(); // Alternativo puede autocompletar
                if (normalizedInorganic.nameCanComplete(input) || normalizedInorganic.searchTagsCanComplete(input))
                    return normalizedInorganic.getOriginalName(); // Nombre o una etiqueta puede autocompletar
            }
        }

        return completion;
    }

    // BÚSQUEDAS ---------------------------------------------------------------------

    protected InorganicResult searchFromCompletion(String completion) {
        InorganicResult inorganicResult;

        Optional<InorganicModel> buscado = searchInDatabase(completion);
        if (buscado.isPresent())
            inorganicResult = new InorganicResult(buscado.get());
        else {
            logger.error("La compleción: \"" + completion + "\" no se encuentra.");
            inorganicResult = notFoundInorganic;
        }

        metricsService.contarInorganicoAutocompletado();
        metricsService.contarInorganicoBuscado(inorganicResult.getPresent(), false);

        return inorganicResult;
    }

    protected InorganicResult search(String input, Boolean isPicture) {
        InorganicResult inorganicResult;

        Optional<InorganicModel> searchedInMemory = searchInDatabase(input); // Flowchart #0

        // Flowchart #1
        if (searchedInMemory.isEmpty()) { // No se encuentra en la DB
            Optional<SearchResult> searchResult = Optional.empty();

            // Flowchart #2
            if (canGoogleSearch()) {
                searchResult = tryGoogleSeach(input);

                metricsService.contarGoogle(searchResult.isPresent() && searchResult.get().found, isPicture);
            }
            // Flowchart #3
            if (searchResult.isEmpty() && canFreeBingSearch()) { // Hubo con Google o no está disponible
                searchResult = tryFreeBingSearch(input);

                metricsService.contarBing(searchResult.isPresent() && searchResult.get().found, isPicture);
            }
            // Flowchart #4
            if (searchResult.isEmpty() && canPaidBingSearch()) { // Hubo con Bing gratis o no está disponible
                searchResult = tryPaidBingSearch(input);

                metricsService.contarBingPago();
                metricsService.contarBing(searchResult.isPresent() && searchResult.get().found, isPicture);
            }

            // Flowchart #0 ó #5
            if (searchResult.isPresent() && searchResult.get().found) { // Se ha podido encontrar con Google o Bing
                String[] words = searchResult.get().title.trim().split(" ");

                String firstWord = words[0];
                if (firstWord.equals("ácido"))
                    firstWord += words[1];

                // Flowchart #0
                searchedInMemory = searchInDatabase(firstWord); // Suele ser la fórmula

                // Flowchart #5
                if (searchedInMemory.isEmpty()) { // Parece no estar en la DB
                    Optional<InorganicModel> parsed = tryParseFQ(searchResult.get().address);

                    if (parsed.isPresent()) { // Escaneado correctamente
                        searchedInMemory = searchInDatabase(parsed.get().getName());

                        if (searchedInMemory.isEmpty()) { // En efecto, no estaba en la DB
                            Float molecularMass =  molecularMassService.tryCalculateMolecularMassOf(
                                    parsed.get().getFormula());

                            if(molecularMass != null)
                                parsed.get().setMolecularMass(molecularMass);

                            inorganicResult = new InorganicResult(parsed.get());
                            inorganicRepository.save(parsed.get());

                            logger.info("Nuevo inorgánico: " + parsed.get());
                            metricsService.contarInorganicoNuevo();
                        } else { // Realmente sí estaba en la DB
                            inorganicResult = new InorganicResult(searchedInMemory.get());

                            logger.warn("El inorgánico parseado \"" + input + "\" era: " +
                                    searchedInMemory.get().getId());
                        }
                    } else inorganicResult = notFoundInorganic;
                }
                // Flowchart #6
                else { // Ya estaba en la DB
                    inorganicResult = new InorganicResult(searchedInMemory.get());
                    logger.warn("El inorgánico buscado en la web \"" + input + "\" era: " + searchedInMemory.get());
                }
            }
            // Flowchart #7
            else { // No se ha podido encontrar ni con Google ni con Bing
                inorganicResult = notFoundInorganic; // Temporal
                // Sugerencia... TODO

                logger.warn("No se ha encontrado el inorgánico \"" + input + "\".");
            }
        }
        // Flowchart #6
        else {
            inorganicResult = new InorganicResult(searchedInMemory.get()); // Está en la DB
        }

        metricsService.contarInorganicoBuscado(inorganicResult.getPresent(), isPicture);

        return inorganicResult;
    }

    // INTERNOS ----------------------------------------------------------------------

    // Flowchart #0
    private Optional<InorganicModel> searchInDatabase(String input) {
        input = Normalized.of(input);

        for (InorganicModel inorganico : inorganicRepository.findAllByOrderBySearchCountDesc())
            if (input.equals(Normalized.of(inorganico.getFormula()))
                    || input.equals(Normalized.of(inorganico.getName()))
                    || input.equals(Normalized.of(inorganico.getAlternativeName()))
                    || inorganico.getEtiquetasString().contains(input)) {
                nuevaBusqueda(inorganico);
                return Optional.of(inorganico);
            }

        return Optional.empty();
    }

    private Boolean canGoogleSearch() {
        if(!settingsService.getGoogleON())
            return false;

        int busquedas = metricsService.getBusquedasGoogle();

        if (busquedas == settingsService.getBingPagoLimite() - 1)
            logger.warn("Búsquedas de Google diarias superadas.");

        return busquedas < settingsService.getGoogleLimite();
    }

    private Boolean canFreeBingSearch() {
        // Cuando devuelve 403 se avisa de que se han superado las de ese mes
        return settingsService.getBingGratisON();
    }

    private Boolean canPaidBingSearch() {
        if(!settingsService.getBingPagoON())
            return false;

        int busquedas = metricsService.getBusquedasBingPago();

        if (busquedas == settingsService.getBingPagoLimite() - 1)
            logger.warn("Búsquedas de Bing de pago diarias superadas.");

        return busquedas < settingsService.getBingPagoLimite();
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
        SearchResult searchResult = new SearchResult();

        Download conexion = new Download(settingsService.getGoogleURL(), input);
        conexion.setProperty("Accept", "application/json");
        JSONObject respuesta = new JSONObject(conexion.getText());

        if (respuesta.getJSONObject("searchInformation").getInt("totalResults") > 0) {
            JSONObject resultado = respuesta.getJSONArray("items").getJSONObject(0);

            searchResult.found = true;
            searchResult.title = resultado.getString("title");
            searchResult.address = resultado.getString("formattedUrl"); // "www.fq.com/..."
        } else {
            searchResult.found = false;

            logger.warn("No se ha encontrado \"" + input + "\" en Google.");
        }

        return searchResult;
    }
    
    // Flowchart #3
    private Optional<SearchResult> tryFreeBingSearch(String input) {
        return tryBingSearch(input, settingsService.getBingGratisKey(), "gratis");
    }
    
    // Flowchart #4
    private Optional<SearchResult> tryPaidBingSearch(String input) {
        return tryBingSearch(input, settingsService.getBingPagoKey(), "pago");
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

        Download conexion = new Download(settingsService.getBingURL(), input);
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
    private Optional<InorganicModel> tryParseFQ(String address) {
        // FQ subdirectories that are NOT compounds:
        if (address.matches("^.*?(acidos-carboxilicos|alcanos|alcoholes|aldehidos|alquenos|alquinos|amidas|" +
                "aminas|anhidridos|anhidridos-organicos|aromaticos|buscador|cetonas|cicloalquenos|ejemplos|" +
                "ejercicios|eteres|halogenuros|hidracidos|hidroxidos|hidruros|hidruros-volatiles|inorganica|" +
                "nitrilos|organica|oxidos|oxisales|oxoacidos|peroxidos|politica-privacidad|sales-neutras|" +
                "sales-volatiles).*$") || address.matches("^.*?(.com)/?$"))
            return Optional.empty();

        try {
            Download conexion = new Download(address);
            conexion.setProperty("User-Agent", settingsService.getUserAgent());

            FormulacionQuimicaPage fqPage = new FormulacionQuimicaPage(conexion.getText());
            InorganicModel parsedInorganic = fqPage.getParsedInorganic();

            if (parsedInorganic != null)
                return Optional.of(parsedInorganic);
            else {
                logger.error("No se pudo escanear la dirección \"" + address + "\".");
                return Optional.empty();
            }
        } catch (Exception exception) {
            logger.error("Excepción al escanear la dirección \"" + address + "\": " + exception);
            return Optional.empty();
        }
    }

    // Incrementa el contador de búsquedas de un inorgánico porque ha sido buscado
    private void nuevaBusqueda(InorganicModel buscado) {
        buscado.registrarBusqueda();
        inorganicRepository.save(buscado);
    }

}
