package com.quimify.api.inorganic;

import com.quimify.api.Normalized;
import com.quimify.api.error.ErrorService;
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
import java.util.*;
import java.util.stream.Collectors;

// This class implements the logic behind HTTP methods in "/inorganic".

@Service
public
class InorganicService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    InorganicRepository inorganicRepository; // DB connection

    @Autowired
    InorganicPageComponent inorganicPageComponent; // Inorganic web pages logic

    @Autowired
    MolecularMassService molecularMassService; // Molecular masses logic

    @Autowired
    SettingsService settingsService; // Settings logic

    @Autowired
    ErrorService errorService; // API errors logic

    @Autowired
    MetricsService metricsService; // Daily metrics logic

    private static final List<InorganicSearchTagModel> searchTags = new ArrayList<>(); // For autocompletion

    protected static final InorganicResult notFoundInorganic = new InorganicResult(); // Handy
public void foo() {
    tryParseFQ("https://www.formulacionquimica.com/H2O2/");
    tryParseFQ("https://www.formulacionquimica.com/BaO2/");
}
    // Administration:

    public void refreshAutocompletion() {
        List<InorganicSearchTagModel> newSearchTags = inorganicRepository.findAllByOrderBySearchCountDesc().stream()
                .flatMap(inorganicModel -> inorganicModel.getSearchTags().stream())
                .collect(Collectors.toList());

        searchTags.clear();
        searchTags.addAll(newSearchTags);

        logger.info("Inorganic search tags updated in memory.");
    }

    // Client autocompletion:

    protected String autoComplete(String input) { // TODO clean code
        String completion = "";

        String normalizedInput = Normalized.of(input);

        for(InorganicSearchTagModel searchTag : searchTags)
            if(searchTag.getNormalizedTag().startsWith(normalizedInput)) {
                Optional<InorganicModel> inorganicModel = inorganicRepository.findBySearchTagsContaining(searchTag);

                if(inorganicModel.isPresent()) {
                    completion = inorganicModel.get().getStockName();
                    if(completion != null && Normalized.of(completion).startsWith(normalizedInput))
                        return completion;

                    completion = inorganicModel.get().getSystematicName();
                    if(completion != null && Normalized.of(completion).startsWith(normalizedInput))
                        return completion;

                    completion = inorganicModel.get().getTraditionalName();
                    if(completion != null && Normalized.of(completion).startsWith(normalizedInput))
                        return completion;

                    completion = inorganicModel.get().getOtherName();
                    if(completion != null && Normalized.of(completion).startsWith(normalizedInput))
                        return completion;

                    return inorganicModel.get().getFormula(); // Formula or a search tag
                }
                else errorService.saveError("Search tag not in DB", searchTag.getNormalizedTag(), this.getClass());
            }

        return completion;
    }

    // Client searching --------------------------------------------------------------

    protected InorganicResult searchFromCompletion(String completion) {
        InorganicResult inorganicResult;

        Optional<InorganicModel> searchedInorganic = searchInDatabase(completion);
        if (searchedInorganic.isPresent())
            inorganicResult = new InorganicResult(searchedInorganic.get());
        else {
            errorService.saveError("Completion not in DB", completion, this.getClass());
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
                searchResult = tryGoogleSearch(input);

                metricsService.contarGoogle(searchResult.isPresent() && searchResult.get().found, isPicture);
            }
            // Flowchart #3
            if (searchResult.isEmpty() && canFreeBingSearch()) { // Google API not available or an error occurred
                searchResult = tryFreeBingSearch(input);

                metricsService.contarBing(searchResult.isPresent() && searchResult.get().found, isPicture);
            }
            // Flowchart #4
            if (searchResult.isEmpty() && canPaidBingSearch()) { // Free Bing API not available or an error occurred
                searchResult = tryPaidBingSearch(input);

                metricsService.contarBingPago();
                metricsService.contarBing(searchResult.isPresent() && searchResult.get().found, isPicture);
            }

            // Flowchart #0 ó #5
            if (searchResult.isPresent() && searchResult.get().found) { // Found using search engines
                String[] words = searchResult.get().title.trim().split(" ");

                String firstWord = words[0];
                if (firstWord.equals("ácido"))
                    firstWord += words[1];

                // Flowchart #0
                searchedInMemory = searchInDatabase(firstWord); // Suele ser la fórmula

                // Flowchart #5
                if (searchedInMemory.isEmpty()) { // Parece no estar en la DB
                    Optional<InorganicModel> parsedInorganic = tryParseFQ(searchResult.get().address);

                    if (parsedInorganic.isPresent()) { // Escaneado correctamente
                        InorganicModel parsed = parsedInorganic.get();

                        searchedInMemory = searchInDatabase(parsed.getFormula()); // DB lookup
                        if (searchedInMemory.isEmpty()) { // En efecto, no estaba en la DB
                            // Molecular mass:
                            Float molecularMass =  molecularMassService.tryMolecularMassOf(parsed.getFormula());
                            parsed.setMolecularMass(String.format("%.2f", molecularMass).replace(",", "."));

                            // Result:
                            inorganicResult = new InorganicResult(parsed);

                            // Stored:
                            inorganicRepository.save(parsed); // In database
                            searchTags.addAll(parsed.getSearchTags()); // Cached in memory

                            // Metrics:
                            metricsService.contarInorganicoNuevo();
                            logger.info("Nuevo inorgánico: " + parsed);
                        } else { // Realmente sí estaba en la DB
                            inorganicResult = new InorganicResult(searchedInMemory.get());
                            logger.warn("El parseado \"" + input + "\" era: " + searchedInMemory.get().getId());
                        }
                    } else inorganicResult = notFoundInorganic;
                }
                // Flowchart #6
                else { // Ya estaba en la DB
                    inorganicResult = new InorganicResult(searchedInMemory.get());
                    logger.warn("El buscado en la web \"" + input + "\" era: " + searchedInMemory.get());
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

    // Private methods ---------------------------------------------------------------

    // Flowchart #0
    private Optional<InorganicModel> searchInDatabase(String input) {
        String normalizedInput = Normalized.of(input);

        for (InorganicModel inorganicModel : inorganicRepository.findAllByOrderBySearchCountDesc())
            if (inorganicModel.getSearchTagsAsStrings().contains(normalizedInput)) {
                // Updates search counter:
                inorganicModel.countSearch();
                inorganicRepository.save(inorganicModel);

                // Result:
                return Optional.of(inorganicModel);
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
    private Optional<SearchResult> tryGoogleSearch(String input) {
        Optional<SearchResult> searchResult;

        try {
            searchResult = Optional.of(googleSearch(input));
        } catch (Exception exception) {
            searchResult = Optional.empty();

            if (exception.toString().contains("Server returned HTTP response code: 429"))
                logger.warn("Google ha devuelto HTTP 429.");
            else errorService.saveError("IOException Google: " + input, exception.toString(), this.getClass());
        }

        return searchResult;
    }

    // Flowchart #2
    private SearchResult googleSearch(String input) throws Exception {
        SearchResult searchResult = new SearchResult();

        Download connection = new Download(settingsService.getGoogleURL(), input);
        connection.setProperty("Accept", "application/json");
        JSONObject response = new JSONObject(connection.getText());

        if (response.getJSONObject("searchInformation").getInt("totalResults") > 0) {
            JSONObject result = response.getJSONArray("items").getJSONObject(0);

            searchResult.found = true;
            searchResult.title = result.getString("title");
            searchResult.address = result.getString("formattedUrl"); // "www.fq.com/..."
        } else {
            searchResult.found = false;

            logger.warn("No se ha encontrado \"" + input + "\" en Google.");
        }

        return searchResult;
    }
    
    // Flowchart #3
    private Optional<SearchResult> tryFreeBingSearch(String input) {
        return tryBingSearch(input, settingsService.getBingGratisKey(), "free Bing");
    }
    
    // Flowchart #4
    private Optional<SearchResult> tryPaidBingSearch(String input) {
        return tryBingSearch(input, settingsService.getBingPagoKey(), "paid Bing");
    }
    
    // Flowchart #3, #4
    private Optional<SearchResult> tryBingSearch(String input, String apiKey, String apiName) {
        Optional<SearchResult> searchResult;

        try {
            searchResult = Optional.of(bingSearch(input, apiKey));
        } catch (IOException exception) {
            searchResult = Optional.empty();

            if (exception.toString().contains("HTTP response code: 403"))
                logger.warn(apiName + " ha devuelto HTTP 403.");
            else errorService.saveError("IOException " + apiName + ": " + input, exception.toString(), this.getClass());
        } catch (Exception exception) {
            searchResult = Optional.empty();

            errorService.saveError("Exception apiName: " + input, exception.toString(), this.getClass());
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
    private Optional<InorganicModel> tryParseFQ(String url) {
        try {
            return Optional.ofNullable(inorganicPageComponent.parseInorganic(url, settingsService.getUserAgent()));
        } catch (Exception exception) {
            errorService.saveError("Exception parsing FQPage: " + url, exception.toString(), this.getClass());
            return Optional.empty();
        }
    }

}
