package com.quimify.api.inorganico;

import com.quimify.api.Normalizado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.util.Optional;

// Esta clase contiene el código para analizar una página de FQ.com.

public class PaginaFQ {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String pagina; // Documento HTML

    // --------------------------------------------------------------------------------

    // Flowchart #5
    public Optional<InorganicoModel> escanearInorganico() {
        Optional<InorganicoModel> resultado = Optional.of(new InorganicoModel());

        int indice = indexAfterIn("<h1>", pagina);

        if(indice == -1) {
            logger.error("La siguiente página de FQ no tiene <h1>: " + pagina);
            return Optional.empty();
        }

        pagina = pagina.substring(indice); // Lo relevante

        // Escanea el nombre y la fórmula:

        String nombre, formula;

        indice = indexAfterIn("/", pagina);
        int etiqueta_cerrada = indexAfterIn("</", pagina);

        if(indice != etiqueta_cerrada) { // Como en "Co2(CO3)3 / carbonato de cobalto (III)</h1>..."
            nombre = pagina.substring(indice + 1, etiqueta_cerrada - 2);
            formula = pagina.substring(0, indice - 2);
        } else { // Como en "metano</h1>..."
            nombre = pagina.substring(0, etiqueta_cerrada - 2);

            indice = indexAfterIn(">Fórmula:", pagina);
            if(indice == -1)
                indice = indexAfterIn("\"frm\">", pagina);

            formula = pagina.substring(indice);
            formula = formula.substring(0, indexAfterIn("</p>", formula) - 4);

            formula = formula.replace("</b>", "").replace("<sub>", "")
                    .replace("</sub>", "").replace(" ", "");
        }

        /* Escanea el nombre alternativo siguiendo este esquema:
            Ácidos:
                nombre = tradicional > stock > sistemática
                alternativo = nombre actual
            Otros:
                alternativo = stock > sistemática > tradicional
        */

        String alternativo = null;

        int indice_tradicional = indexAfterIn("tradicional:</b>", pagina);
        int indice_stock = indexAfterIn("stock:</b>", pagina);

        // Si no pone "ácido" ni en el título ni en tradicional, si tuviera
        if(!((indexAfterIn("ácido", nombre) != -1) ||
                (indice_tradicional != -1 && indexAfterIn("ácido",
                        pagina.substring(indice_tradicional, indice_tradicional + 6)) != -1))) {

            if(indice_stock != -1) {
                // Hay nomenclatura stock en la página
                String dato = pagina.substring(indice_stock + 1);
                alternativo = dato.substring(0, indexAfterIn("</p>", dato) - 4);
            }
            if(indice_stock == -1 || nombre.contentEquals(alternativo)) {
                // No había stock o había pero es igual
                int indice_sistematica = indexAfterIn("sistemática:</b>", pagina);

                if(indice_sistematica != -1) {
                    // Hay sistemática
                    String dato = pagina.substring(indice_sistematica + 1);
                    alternativo = dato.substring(0, indexAfterIn("</p>", dato) - 4);
                }
                if(indice_sistematica == -1 || nombre.contentEquals(alternativo)) {
                    // No había sistemática o había pero es igual
                    if(indice_tradicional != -1) {
                        String dato = pagina.substring(indice_tradicional + 1);
                        alternativo = dato.substring(0, indexAfterIn("</p>", dato) - 4);
                    }
                }
            }
        }
        else { // Pone "ácido" en el título o en tradicional si lo tuviera
            if (indice_tradicional != -1) { // Hay tradicional
                alternativo = nombre;
                String dato = pagina.substring(indice_tradicional + 1);
                nombre = dato.substring(0, indexAfterIn("</p>", dato) - 4);
            }
            if (indice_tradicional == -1 || nombre.contentEquals(alternativo)) {
                // No había tradicional o había pero es igual
                if (indice_stock != -1) { // Hay nomenclatura stock en la página
                    String dato = pagina.substring(indice_stock + 1);
                    alternativo = dato.substring(0, indexAfterIn("</p>", dato) - 4);
                }
                if (indice_stock == -1 || nombre.contentEquals(alternativo)) {
                    // No había stock o había pero es igual
                    if (pagina.contains("sistemática:</b>")) { // Hay sistemática
                        String dato = pagina
                                .substring(indexAfterIn("sistemática:</b>", pagina) + 1);
                        alternativo = dato.substring(0, indexAfterIn("</p>", dato) - 4);
                    }
                }
            }
        }

        // Correcciones:

        if(nombre.contains("br/>")) { // Error con algunos orgánicos
            nombre = nombre.substring(4);
            resultado.get().setPremium(true);
        }

        if(alternativo != null) {
            if(alternativo.contains("oxo")) // Nomenclatura obsoleta
                alternativo = null;
            else if(alternativo.contains("br/>")) { // Error con algunos orgánicos
                alternativo = alternativo.substring(4);
                resultado.get().setPremium(true);
            }

            if(alternativo != null && nombre.contentEquals(alternativo)) // Duplicado
                alternativo = null;
        }

        nombre = nombre.replaceAll(" \\(", "("); // Ej.: óxido de hierro (II) -> óxido de hierro(II)

        if(alternativo != null)
            alternativo = alternativo.replaceAll(" \\(", "(");

        // Etiquetas:

        String etiqueta = nombre.replace("ácido ", "");
        if(!etiqueta.contentEquals(nombre))
            resultado.get().nuevaEtiqueta(new EtiquetaModel(Normalizado.of(etiqueta)));

        if(alternativo != null) {
            etiqueta = alternativo.replace("ácido ", "");
            if(!etiqueta.contentEquals(alternativo))
                resultado.get().nuevaEtiqueta(new EtiquetaModel(Normalizado.of(etiqueta)));
        }

        // Fin:

        resultado.get().setFormula(formula);
        resultado.get().setNombre(nombre);
        resultado.get().setAlternativo(alternativo);

        // Características numéricas:

        resultado.get().setMasa(masaFQ());
        resultado.get().setFusion(temperaturaFQ("fusión"));
        resultado.get().setEbullicion(temperaturaFQ("ebullición"));
        resultado.get().setDensidad(densidadFQ());

        return resultado;
    }

    private int indexAfterIn(String substring, String string) {
        int index = string.indexOf(substring);
        return (index != -1) ? index + substring.length() : index;
    }

    private String masaFQ() {
        String masa;

        int indice = indexAfterIn("Masa molar:", pagina);
        if(indice == -1)
            indice = indexAfterIn("Masa Molar:", pagina);
        if(indice == -1)
            indice = indexAfterIn("Peso Molecular:", pagina);
        if(indice != -1) { // Aparece la masa molecular
            masa = pagina.substring(indice + 1);
            masa = masa.substring(0, indexAfterIn("</", masa) - 2);

            indice = indexAfterIn("g", masa);
            if(indice != -1) { // Aparece la unidad
                masa = masa.substring(0, indice - 1);

                masa = masa.replace(" ", "").replace(",", ".");
                masa = masa.replace("a", "-"); // Otro formato de intervalo
                masa = soloNumeros(masa);
                masa = primeroDelIntervalo(masa);
                masa = truncarDosDecimales(masa);
                masa = quitarCerosDecimalesALaDerecha(masa);
            }
            else masa = null;
        }
        else masa = null;

        return masa;
    }

    private String temperaturaFQ(String tipo) {
        String temperatura;

        int indice = indexAfterIn("Punto de " + tipo + ":", pagina);
        if(indice == -1)
            indice = indexAfterIn("Temperatura de " + tipo + ":", pagina);
        if(indice != -1) {
            temperatura = pagina.substring(indice + 1);
            temperatura = temperatura.substring(0, indexAfterIn("</", temperatura) - 2);

            indice = indexAfterIn("°", temperatura);
            if(indice == -1)
                indice = indexAfterIn("º", temperatura); // Es distinto al anterior
            if(indice != -1) { // Aparece el símbolo de grado
                temperatura = temperatura.substring(0, indice - 1);

                temperatura = temperatura.replace(" ", "").replace(",", ".");
                temperatura = temperatura.replace("a", "-"); // Otro formato de intervalo
                temperatura = soloNumeros(temperatura);
                temperatura = primeroDelIntervalo(temperatura);
                temperatura = String.valueOf(273.15 + Float.parseFloat(temperatura));

                temperatura = truncarDosDecimales(temperatura);
                temperatura = quitarCerosDecimalesALaDerecha(temperatura);
            }
            else temperatura = null;
        }
        else temperatura = null;

        return temperatura;
    }

    private String densidadFQ() {
        String densidad;

        int indice = indexAfterIn("Densidad:", pagina);
        if (indice != -1) { // Aparece la densidad
            densidad = pagina.substring(indice + 1);
            densidad = densidad.substring(0, indexAfterIn("</", densidad) - 2);

            indice = indexAfterIn("g", densidad);
            if (indice != -1) { // Aparece la unidad ("g", "Kg", "kg")
                boolean en_kilogramos;
                if (densidad.charAt(indice - 2) == 'k' || densidad.charAt(indice - 2) == 'K') {
                    densidad = densidad.substring(0, indice - 2);
                    en_kilogramos = true;
                }
                else {
                    densidad = densidad.substring(0, indice - 1);
                    en_kilogramos = false;
                }

                densidad = densidad.replace(" ", "").replace(",", ".");
                densidad = densidad.replace("a", "-"); // Otro formato de intervalo
                densidad = soloNumeros(densidad);
                densidad = primeroDelIntervalo(densidad);

                if(en_kilogramos) {
                    float valor_densidad = Float.parseFloat(densidad);
                    valor_densidad /= 1000; // De kg/m3 a g/cm3

                    NumberFormat formato = NumberFormat.getInstance();
                    formato.setGroupingUsed(false);
                    formato.setMaximumFractionDigits(6);
                    densidad = formato.format(valor_densidad).replace(",", ".");

                    densidad = tresDecimalesSignificativos(densidad);
                }

                densidad = quitarCerosDecimalesALaDerecha(densidad);
            }
            else densidad = null;
        }
        else densidad = null;

        return densidad;
    }

    // Ej.: "-1,104 ºC - 0.34 " -> "-1,104-0.34"
    private String soloNumeros(String dato) {
        StringBuilder resultado = new StringBuilder();

        for(int i = 0; i < dato.length(); i++)
            if(esNumero(dato.charAt(i)))
                resultado.append(dato.charAt(i));

        return resultado.toString();
    }

    private static boolean esNumero(char c) {
        return (c >= '0' && c <= '9') || c == '-' || c == '.'; // Signo negativo y punto decimal
    }

    // Ej.: "12.104 - 13.2 ºC" -> "12.104"
    // Ej.: "12.104 a 13.2 ºC" -> "12.104"
    private String primeroDelIntervalo(String dato) {
        String resultado;

        dato = dato.trim();
        if(indexAfterIn("-", dato.substring(1)) != -1) // Por si es el signo negativo
            resultado = dato.split("-", 2)[0];
        else resultado = dato;

        return resultado;
    }

    // Ej.: "13.450" -> "13.45"
    // Ej.: "6.000" -> "6"
    private String quitarCerosDecimalesALaDerecha(String numero) {
        if(indexAfterIn(".", numero) != -1)
            while (numero.charAt(numero.length() - 1) == '0')
                numero = numero.substring(0, numero.length() - 1);

        if(numero.charAt(numero.length() - 1) == '.') // Por si se queda el punto suelto
            numero = numero.substring(0, numero.length() - 1);

        return numero;
    }

    // Ej.: "14.457 -> 14.45"
    private String truncarDosDecimales(String numero) {
        int punto = indexAfterIn(".", numero);
        if(punto != -1) {
            punto += 2;
            if(numero.length() > punto) // Hay más de dos decimales
                numero = numero.substring(0, punto);
        }

        return numero;
    }

    // Ej.: "X.000ABCD" -> "X.000ABC"
    private String tresDecimalesSignificativos(String numero) {
        int punto = indexAfterIn(".", numero);
        if(punto != -1)
            for(int i = punto, decimales_significativos = 0; i < numero.length() && decimales_significativos < 3; i++)
                if(numero.charAt(i) != '0')
                    if(++decimales_significativos == 3)
                        numero = numero.substring(0, i + 1);

        return numero;
    }

    // --------------------------------------------------------------------------------

    // Getters y setters:

    PaginaFQ(String pagina) {
        this.pagina = pagina;
    }

}
