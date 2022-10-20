package com.quimify.api.inorganico;

import com.quimify.api.Normalizado;
import com.quimify.api.masa_molecular.MasaMolecularResultado;
import com.quimify.api.masa_molecular.MasaMolecularService;
import com.quimify.api.utils.Download;
import com.quimify.api.configuracion.ConfiguracionService;
import com.quimify.api.metricas.MetricasService;
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

    public String autoCompletar(String input) {
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

    public InorganicoResultado buscarPorComplecion(String complecion) {
        InorganicoResultado resultado;

        Optional<InorganicoModel> buscado = buscarMemoriaPrincipal(complecion);
        if (buscado.isPresent()) {
            resultado = new InorganicoResultado(buscado.get());
            nuevaBusqueda(buscado.get());
        } else {
            logger.error("La compleción: \"" + complecion + "\" no se encuentra.");
            resultado = NO_ENCONTRADO;
        }

        metricasService.contarInorganicoAutocompletado();
        metricasService.contarInorganicoBuscado(resultado.getEncontrado(), false);

        return resultado;
    }

    public InorganicoResultado buscar(String input, Boolean foto) {
        InorganicoResultado resultado;

        Optional<InorganicoModel> buscado = buscarMemoriaPrincipal(input); // Flowchart #0

        // Flowchart #1
        if (buscado.isEmpty()) { // No se encuentra en la DB
            Optional<BusquedaWeb> busqueda_web = Optional.empty();

            // Flowchart #2
            if (disponibleGoogle()) {
                busqueda_web = tryBuscarGoogle(input);

                metricasService.contarGoogle(busqueda_web.isPresent() && busqueda_web.get().encontrado, foto);
            }
            // Flowchart #3
            if (busqueda_web.isEmpty() && disponibleBingGratis()) { // Hubo con Google o no está disponible
                busqueda_web = tryBuscarBingGratis(input);

                metricasService.contarBing(busqueda_web.isPresent() && busqueda_web.get().encontrado, foto);
            }
            // Flowchart #4
            if (busqueda_web.isEmpty() && disponibleBingPago()) { // Hubo con Bing gratis o no está disponible
                busqueda_web = tryBuscarBingPago(input);

                metricasService.contarBingPago();
                metricasService.contarBing(busqueda_web.isPresent() && busqueda_web.get().encontrado, foto);
            }

            // Flowchart #0 ó #5
            if (busqueda_web.isPresent() && busqueda_web.get().encontrado) { // Se ha podido encontrar con Google o Bing
                String[] palabras = busqueda_web.get().titulo.trim().split(" ");

                String primera_palabra = palabras[0];
                if (primera_palabra.equals("ácido"))
                    primera_palabra += palabras[1];

                // Flowchart #0
                buscado = buscarMemoriaPrincipal(primera_palabra); // Suele ser la fórmula

                // Flowchart #5
                if (buscado.isEmpty()) { // Parece no estar en la DB
                    Optional<InorganicoModel> parsed = tryParseFQ(busqueda_web.get().direccion);

                    if (parsed.isPresent()) { // Escaneado correctamente
                        buscado = buscarMemoriaPrincipal(parsed.get().getNombre());

                        if (buscado.isEmpty()) { // En efecto, no estaba en la DB
                            if (parsed.get().getMasa() == null) {
                                MasaMolecularResultado masaMolecularResultado =
                                        masaMolecularService.tryMasaMolecularDe(parsed.get().getFormula());

                                if (masaMolecularResultado.getEncontrado())
                                    parsed.get().setMasa(masaMolecularResultado.getMasa().toString());
                            }

                            resultado = new InorganicoResultado(parsed.get());

                            inorganicoRepository.save(parsed.get());

                            logger.info("Nuevo inorgánico: " + parsed.get());
                            metricasService.contarInorganicoNuevo();
                        } else { // Realmente sí estaba en la DB
                            resultado = new InorganicoResultado(buscado.get());

                            logger.warn("El inorgánico parseado \"" + input + "\" era: " + buscado.get().getId());
                        }
                    } else resultado = NO_ENCONTRADO;
                }
                // Flowchart #6
                else { // Ya estaba en la DB
                    resultado = new InorganicoResultado(buscado.get());
                    logger.warn("El inorgánico buscado en la web \"" + input + "\" era: " + buscado.get());
                }
            }
            // Flowchart #7
            else { // No se ha podido encontrar ni con Google ni con Bing
                resultado = NO_ENCONTRADO; // Temporal
                // Sugerencia... TODO

                logger.warn("No se ha encontrado el inorgánico \"" + input + "\".");
            }
        }
        // Flowchart #6
        else {
            resultado = new InorganicoResultado(buscado.get()); // Está en la DB
        }

        metricasService.contarInorganicoBuscado(resultado.getEncontrado(), foto);

        return resultado;
    }

    // INTERNOS ----------------------------------------------------------------------

    // Flowchart #0
    private Optional<InorganicoModel> buscarMemoriaPrincipal(String input) {
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

    private Boolean disponibleGoogle() {
        boolean superadas = metricasService.getBusquedasGoogle() >= configuracionService.getGoogleLimite();

        if (superadas && configuracionService.getGoogleON())
            logger.warn("Búsquedas de Google superadas");

        return !superadas && configuracionService.getGoogleON();
    }

    private Boolean disponibleBingGratis() {
        // Cuando devuelve 403 se avisa de que se han superado las de ese mes
        return configuracionService.getBingGratisON();
    }

    private Boolean disponibleBingPago() {
        boolean superadas = metricasService.getBusquedasBingPago() >= configuracionService.getBingPagoLimite();

        if (superadas && configuracionService.getBingPagoON())
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
        } catch (Exception exception) {
            busqueda_web = Optional.empty();

            if (exception.toString().contains("Server returned HTTP response code: 429"))
                logger.warn("Google ha devuelto HTTP 429.");
            else logger.error("IOException al buscar \"" + input + "\" en Google: " + exception);
        }

        return busqueda_web;
    }

    // Flowchart #2
    private BusquedaWeb buscarGoogle(String input) throws Exception {
        BusquedaWeb busqueda_web = new BusquedaWeb();

        Download conexion = new Download(configuracionService.getGoogleURL(), input);
        conexion.setPropiedad("Accept", "application/json");
        JSONObject respuesta = new JSONObject(conexion.getTexto());

        if (respuesta.getJSONObject("searchInformation").getInt("totalResults") > 0) {
            JSONObject resultado = respuesta.getJSONArray("items").getJSONObject(0);

            busqueda_web.encontrado = true;
            busqueda_web.titulo = resultado.getString("title");
            busqueda_web.direccion = resultado.getString("formattedUrl"); // "www.fq.com/..."
        } else {
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
        } catch (IOException exception) {
            busqueda_web = Optional.empty();

            if (exception.toString().contains("HTTP response code: 403"))
                logger.warn("Bing gratis ha devuelto HTTP 403.");
            else logger.error("IOException al buscar \"" + input + "\" en Bing: " + exception);
        } catch (Exception exception) {
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
        } catch (IOException exception) {
            busqueda_web = Optional.empty();

            if (exception.toString().contains("HTTP response code: 403"))
                logger.warn("Bing de pago ha devuelto HTTP 403.");
            else logger.error("IOException al buscar \"" + input + "\" en Bing de pago: " + exception);
        } catch (Exception exception) {
            busqueda_web = Optional.empty();

            logger.error("Exception al buscar \"" + input + "\" en Bing de pago: " + exception);
        }

        return busqueda_web;
    }

    // Flowchart #3 ó #4
    private BusquedaWeb buscarBing(String input, String key) throws Exception {
        BusquedaWeb busqueda_web = new BusquedaWeb();

        Download conexion = new Download(configuracionService.getBingURL(), input);
        conexion.setPropiedad("Ocp-Apim-Subscription-Key", key);
        JSONObject respuesta = new JSONObject(conexion.getTexto());

        if (respuesta.has("webPages")) {
            JSONObject resultado = respuesta.getJSONObject("webPages")
                    .getJSONArray("value").getJSONObject(0);

            busqueda_web.encontrado = true;
            busqueda_web.titulo = resultado.getString("name");
            busqueda_web.direccion = resultado.getString("url"); // Será "www.fq.com/..."
        } else {
            busqueda_web.encontrado = false;

            logger.warn("No se ha encontrado \"" + input + "\" en Bing.");
        }

        return busqueda_web;
    }

    // Flowchart #5
    private Optional<InorganicoModel> tryParseFQ(String direccion) {
        // FQ subdirectories that are NOT compounds:
        if(direccion.matches("^.*?(acidos-carboxilicos|alcanos|alcoholes|aldehidos|alquenos|alquinos|amidas|" +
                "aminas|anhidridos|anhidridos-organicos|aromaticos|buscador|cetonas|cicloalquenos|ejemplos|" +
                "ejercicios|eteres|halogenuros|hidracidos|hidroxidos|hidruros|hidruros-volatiles|inorganica|" +
                "nitrilos|organica|oxidos|oxisales|oxoacidos|peroxidos|politica-privacidad|sales-neutras|" +
                "sales-volatiles).*$") || direccion.matches("^.*?(.com)/?$"))
            return Optional.empty();

        Optional<InorganicoModel> resultado;

        try {
            Download conexion = new Download(direccion);
            conexion.setPropiedad("User-Agent", configuracionService.getUserAgent());

            PaginaFQ pagina_fq = new PaginaFQ(conexion.getTexto());
            resultado = pagina_fq.escanearInorganico();

            if(resultado.isEmpty())
                logger.error("No se pudo escanear la dirección \"" + direccion + "\".");
        } catch (Exception exception) {
            resultado = Optional.empty();

            logger.error("Excepción al escanear la dirección \"" + direccion + "\": " + exception);
        }

        return resultado;
    }

    // Incrementa el contador de búsquedas de un inorgánico porque ha sido buscado
    private void nuevaBusqueda(InorganicoModel buscado) {
        buscado.registrarBusqueda();
        inorganicoRepository.save(buscado);
    }

}
