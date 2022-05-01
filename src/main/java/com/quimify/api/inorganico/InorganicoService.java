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
import java.text.NumberFormat;
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

        URL url = new URL(configuracionService.getGoogleURL() +
                formatearHTTP(input)); // Parámetro HTTP de búsqueda
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod("GET");
        conexion.setRequestProperty("Accept", "application/json");

        JSONObject respuesta = descargarJSON(conexion);

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

        URL url = new URL(configuracionService.getBingURL() +
                formatearHTTP(input)); // Parámetro HTTP de búsqueda
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestProperty("Ocp-Apim-Subscription-Key", key);

        JSONObject respuesta = descargarJSON(conexion);

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

    // Flowchart #2 ó #3 ó #4
    private String identificador(String titulo) {
        // "H2O / óxido de dihidrógeno", "metanol - www.fq.com", "etanol"...
        int espacio = titulo.indexOf(' ');
        if(espacio > 0)
            titulo = titulo.substring(0, titulo.indexOf(' ')); // "H2O", "metanol", "etanol"...

        return titulo;
    }

    // Flowchart #5
    private InorganicoModel tryParsearFQ(String URL) {
        InorganicoModel resultado;

        try {
            //resultado = parsearFQ(URL);
            resultado = null; // Test
        } catch (Exception e) {
            // ...
            resultado = null;
        }

        return resultado;
    }

    // TODO: reformar esta PUTA BASURA de función de hace año y medio que está FATAL aunque funcione
    // Flowchart #5
    private InorganicoModel parsearFQ(String URL) {
        //Las URL de compuestos de FQ son la fórmula del compuesto
        formula = direccion.substring(indiceDespuesDeEn(".com/", direccion));
        formula = formula.substring(0, formula.length() - 1);

        //Documento HTML de la web del compuesto
        StringBuilder pag_f = new StringBuilder(descargar(direccion));
        //Simplificación de la página a partir del h1
        pag_f = new StringBuilder(pag_f.substring(indiceDespuesDeEn(
                "<h1>", pag_f)));
        //<h1>Co2(CO3)3 / carbonato de cobalto (III)</h1>

        int a = indiceDespuesDeEn("/", pag_f) + 1;
        if(a == indiceDespuesDeEn("</", pag_f) + 1) a = 0;

        //5 son los caracteres de </h1>, que no va incluido
        nombre = pag_f.substring(a, indiceDespuesDeEn("</h1>", pag_f) - 5);

        if(indiceDespuesDeEn("-", formula) != -1){
            a = indiceDespuesDeEn(">Fórmula:", pag_f);
            if(a != -1){
                formula = pag_f.substring(a);
                formula = formula.substring(0, indiceDespuesDeEn("</p>", formula) - 4);
                while (true){
                    a = indiceDespuesDeEn("<sub>", formula);
                    if(a == -1) break;
                    else
                        formula = formula.substring(0, a - 5) + formula.substring(a);
                }
                while (true){
                    a = indiceDespuesDeEn("</sub", formula);
                    if(a == -1) break;
                    else
                        formula = formula.substring(0, a - 5) + formula.substring(a + 1);
                }
            }
            a = indiceDespuesDeEn("\"frm\">", pag_f);
            if(a != -1){
                formula = pag_f.substring(a);
                formula = formula.substring(0, indiceDespuesDeEn("</p>", formula) - 4);
                while (true){
                    a = indiceDespuesDeEn("<sub>", formula);
                    if(a == -1) break;
                    else
                        formula = formula.substring(0, a - 5) + formula.substring(a);
                }
                while (true){
                    a = indiceDespuesDeEn("</sub", formula);
                    if(a == -1) break;
                    else
                        formula = formula.substring(0, a - 5) + formula.substring(a + 1);
                }
            }
            formula = formula.replaceAll(" ", "").replaceAll("</b>", "");
        }

        if(buscarBBDD(formula)){
            Inicio.nucleo.nuevaInstancia(this);
            return true;
        }

        /*
        NORMALES:
            1:  TÍTULO
            2:  STOCK > SISTEMÁTICA > TRADICIONAL
        ÁCIDOS:
            1:  TRADICIONAL > STOCK > SISTEMÁTICA
            2:  TÍTULO (solo si no pone oxo...)
        */

        if(indiceDespuesDeEn("</b>", formula) != -1)
            formula = formula.substring(4);
        int tradicional = indiceDespuesDeEn("tradicional:</b>", pag_f);
        int stock = indiceDespuesDeEn("stock:</b>", pag_f);

        //Si no pone "ácido" ni en el h1 ni en tradicional, si tuviera
        if(!((indiceDespuesDeEn("ácido", nombre) != -1)
                || (tradicional != -1
                && indiceDespuesDeEn("ácido", new StringBuilder(
                pag_f.substring(tradicional, tradicional + 6))) != -1))){

            if (stock != -1) {
                //Hay nomenclatura stock en la página
                StringBuilder pag_f_2 = new StringBuilder(pag_f.substring(stock + 1));
                alternativo = pag_f_2.substring(0,
                        indiceDespuesDeEn("</p>", pag_f_2) - 4);
            }

            if (stock == -1 || nombre.contentEquals(alternativo)) {
                //No había stock o había pero es igual
                int sistematica = indiceDespuesDeEn("sistemática:</b>", pag_f);

                if (sistematica != -1) {
                    //Hay sistemática
                    StringBuilder pag_f2 = new StringBuilder(pag_f.substring(sistematica + 1));
                    alternativo = pag_f2.substring(0,
                            indiceDespuesDeEn("</p>", pag_f2) - 4);
                }

                if (sistematica == -1 || nombre.contentEquals(alternativo)) {
                    //No había sistemática o había pero es igual
                    if (tradicional != -1) {
                        StringBuilder pag_f2 = new StringBuilder(pag_f.substring(tradicional + 1));
                        alternativo = pag_f2.substring(0,
                                indiceDespuesDeEn("</p>", pag_f2) - 4);
                    }
                }
            }

        }
        else{ //Pone "ácido" en el h1 o en tradicional si lo tuviera
            if (tradicional != -1) { //Hay tradicional
                alternativo = nombre;
                StringBuilder pag_f2 = new StringBuilder(pag_f.substring(tradicional + 1));
                nombre = pag_f2.substring(0, indiceDespuesDeEn("</p>", pag_f2) - 4);
            }

            if (tradicional == -1 || nombre.contentEquals(alternativo)) {
                //No había tradicional o había pero es igual
                if (stock != -1) {
                    //Hay nomenclatura stock en la página
                    StringBuilder pag_f_2 = new StringBuilder(pag_f.substring(stock + 1));
                    alternativo = pag_f_2.substring(0,
                            indiceDespuesDeEn("</p>", pag_f_2) - 4);
                }

                if (stock == -1 || nombre.contentEquals(alternativo)) {
                    //No había stock o había pero es igual
                    if (indiceDespuesDeEn("sistemática:</b>", pag_f) != -1) {
                        //Hay sistemática
                        StringBuilder pag_f2 = new StringBuilder(pag_f.substring(indiceDespuesDeEn(
                                "sistemática:</b>", pag_f) + 1));
                        alternativo = pag_f2.substring(0,
                                indiceDespuesDeEn("</p>", pag_f2) - 4);
                    }
                }
            }
        }

        if (nombre.contentEquals(alternativo)) alternativo = null;
        if (indiceDespuesDeEn("oxo", alternativo) != -1) alternativo = null;

        int p = indiceDespuesDeEn("Masa molar:", pag_f);
        if(p == -1) p = indiceDespuesDeEn("Masa Molar:", pag_f);
        if(p != -1){
            try{
                String s = pag_f.substring(p + 1);
                masa = s.substring(0, indiceDespuesDeEn("g", s) - 1)
                        .replaceAll(",",".")
                        .replaceAll(" ", "");

                masa = formatear(masa);
            }catch (Exception e){
                Inicio.nucleo.error(
                        e, "Mas " + direccion);
            }
        }

        p = indiceDespuesDeEn("Densidad:", pag_f);
        if(p != -1){
            String s = pag_f.substring(p + 1);
            s = s.substring(0, indiceDespuesDeEn("<", s) - 1);
            try{
                int pos = indiceDespuesDeEn("g", s);
                if(pos != -1){
                    if(indiceDespuesDeEn("Kg", s) != -1 || indiceDespuesDeEn("kg", s) != -1){
                        pos -= 1;
                        float d = (Float.parseFloat(s.substring(0, pos - 1)
                                .replaceAll(",",".")
                                .replaceAll(" ", "")))
                                / 1000; //Pasa de kg/m3 a g/cm3
                        NumberFormat numberFormat = NumberFormat.getInstance();
                        numberFormat.setGroupingUsed(false);
                        numberFormat.setMaximumFractionDigits(6);
                        densidad = numberFormat.format(d);
                        boolean pase = false;
                        int j = 0;
                        for(int i = 0; i < densidad.length(); i++){
                            if(!pase){
                                if(densidad.charAt(i) == '.') {
                                    pase = true;
                                }
                            }else{
                                if(densidad.charAt(i) != '0') {
                                    j += 1;
                                    if(j == 3) {
                                        densidad = densidad.substring(0, i + 1);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    else densidad = s.substring(0, pos - 1).replaceAll(",",".").replaceAll(" ", "");
                }
                p = indiceDespuesDeEn(".", densidad);
                //16.04
                if(p != -1)
                    while(densidad.charAt(densidad.length() - 1) == '0')
                        densidad = densidad.substring(0, densidad.length() - 1);
                if(densidad.charAt(densidad.length() - 1) == '.')
                    densidad = densidad.substring(0, densidad.length() - 1);
            }catch (Exception e){
                Inicio.nucleo.error(
                        e, "Den " + direccion);
            }
        }

        p = indiceDespuesDeEn("Punto de fusión:", pag_f);
        if(p == -1) p = indiceDespuesDeEn("Temperatura de fusión:", pag_f);
        if(p != -1){
            try{
                String s = pag_f.substring(p + 1);
                s = s.substring(0, indiceDespuesDeEn("<", s) - 1);
                while(s.length() > 0)
                    if(noEsNumero(s.charAt(0))) s = s.substring(1);
                    else break;

                int index = indiceDespuesDeEn("-", s) - 1;
                if(index == -2 || index > 8)
                    index = indiceDespuesDeEn("°", s) - 1;
                if(index == -2 || index > 8)
                    index = indiceDespuesDeEn("º", s) - 1;
                if(index > 0) {
                    p_fusion = formatear(String.valueOf(273.15 +
                            Float.parseFloat(s.substring(0, index)
                                    .replaceAll(",", ".")
                                    .replaceAll(" ", ""))));
                }
            }catch (Exception e){
                Inicio.nucleo.error(
                        e, "Pfu " + direccion);
            }
        }

        p = indiceDespuesDeEn("Punto de ebullición:", pag_f);
        if(p == -1) p = indiceDespuesDeEn("Temperatura de ebullición:", pag_f);
        if(p != -1){
            try{
                String s = pag_f.substring(p + 1);
                while(s.length() > 0)
                    if(noEsNumero(s.charAt(0))) s = s.substring(1);
                    else break;

                int index = indiceDespuesDeEn("-", s) - 1;
                if(index == -2 || index > 8)
                    index = indiceDespuesDeEn("°", s) - 1;
                if(index == -2 || index > 8)
                    index = indiceDespuesDeEn("º", s) - 1;
                if(index > 0) {
                    p_ebullicion = formatear(String.valueOf(273.15 +
                            Float.parseFloat(s.substring(0, index)
                                    .replaceAll(",", ".")
                                    .replaceAll(" ", ""))));
                }
            }catch (Exception e){
                Inicio.nucleo.error(
                        e, "Peb " + direccion);
            }
        }

        if(alternativo != null && indiceDespuesDeEn("br/>", alternativo) != -1){
            alternativo = alternativo.substring(4);
            if (nombre.contentEquals(alternativo)) alternativo = null;
            if(buscarBBDD(alternativo)){
                Inicio.nucleo.nuevaInstancia(this);
                return true;
            }else premium = true;
        }

        if(indiceDespuesDeEn("br/>", nombre) != -1){
            nombre = nombre.substring(4);
            if (nombre.contentEquals(alternativo)) alternativo = null;
            if(buscarBBDD(nombre)){
                Inicio.nucleo.nuevaInstancia(this);
                return true;
            }else premium = true;
        }

        crearIdBusqueda();

        if(buscarBBDD(nombre) || buscarBBDD(alternativo) || buscarBBDD(id)){
            Inicio.nucleo.nuevaInstancia(this);
            return true;
        }

        crearIdBusqueda();
        Inicio.nucleo.nuevoCompuesto(this);

        return true;
    }

    // Flowchart #5
    private int indiceDespuesDeEn(String fragmento, String texto){
        int indice = texto.indexOf(fragmento);
        if(indice != -1)
            indice += fragmento.length();

        return indice;
    }

    // Flowchart #7
    private InorganicoResultado decidirPremium(Integer id, Boolean usuario_premium) {
        InorganicoModel resultado = inorganicoRepository.encontrarPorId(id);

        return (!resultado.getPremium() || usuario_premium)
                ? new InorganicoResultado(resultado) : NO_PREMIUM;
    }

}
