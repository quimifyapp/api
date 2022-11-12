package com.quimify.api.inorganic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;

// Esta clase contiene el código para analizar una página de FQ.com.

class FormulacionQuimicaPage {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String htmlDocument;

    private InorganicModel parsedInorganic;

    // --------------------------------------------------------------------------------

    // Constructor:

    // Flowchart #5
    protected FormulacionQuimicaPage(String htmlDocument) {
        this.htmlDocument = htmlDocument;

        int index = indexAfterIn("<h1>", htmlDocument);

        if(index != -1) {
            this.htmlDocument = htmlDocument.substring(index); // Removes metadata

            parsedInorganic = new InorganicModel();

            // Formula:

            parsedInorganic.setFormula(parseFormula());

            // Names:

            parseAndSetNames();

            // Search tags:

            setSearchTags();

            // Properties:

            parsedInorganic.setMolecularMass(parseMolecularMass());
            parsedInorganic.setDensity(parseDensity());
            parsedInorganic.setMeltingPoint(parseTemperature("fusión"));
            parsedInorganic.setBoilingPoint(parseTemperature("ebullición"));
        }
        else logger.error("La siguiente página de FQ no tiene <h1>: " + htmlDocument);
    }

    // Steps:

    private String parseFormula() {
        String formula;

        int index = indexAfterIn("/", htmlDocument);
        int closingTagIndex = indexAfterIn("</", htmlDocument);

        if(index == closingTagIndex) { // "metano</h1>..."
            index = indexAfterIn(">Fórmula:", htmlDocument);

            if(index == -1)
                index = indexAfterIn("\"frm\">", htmlDocument);

            formula = htmlDocument.substring(index);
            formula = formula.substring(0, indexAfterIn("</p>", formula) - 4);

            formula = formula.replaceAll("(</?sub>)|(</b>)| ", ""); // <sub> or </sub> or </b> or ' '
        } else formula = htmlDocument.substring(0, index - 2); // "Co2(CO3)3 / carbonato de cobalto (III)</h1>..."

        return formula;
    }

    private void parseAndSetNames() {
        int index = indexAfterIn("sistemática:</b>", htmlDocument);
        if (index != -1)
            parsedInorganic.setSystematicName(parseName(index));

        index = indexAfterIn("stock:</b>", htmlDocument);
        if (index != -1)
            parsedInorganic.setStockName(parseName(index));

        index = indexAfterIn("tradicional:</b>", htmlDocument);
        if (index != -1)
            parsedInorganic.setTraditionalName(parseName(index));
    }

    private String parseName(int index) {
        String name = htmlDocument.substring(index + 1);
        name = name.substring(0, indexAfterIn("</p>", name) - 4);
        return correctName(name);
    }

    private String correctName(String name) {
        // Parenthesis:
        name = name.replaceAll(" \\(", "("); // " (II)" -> "(II)"

        // It happens with some organic compounds:
        if(name.contains("br/>")) {
            name = name.replace("br/>", "");
            // isOrganic = true; TODO
        }

        return name;
    }

    private void setSearchTags() {
        parsedInorganic.addSearchTagOf(parsedInorganic.getFormula());

        setNameSearchTag(parsedInorganic.getStockName());
        setNameSearchTag(parsedInorganic.getSystematicName());
        setNameSearchTag(parsedInorganic.getTraditionalName());
    }

    private void setNameSearchTag(String name) {
        if(name != null) {
            parsedInorganic.addSearchTagOf(name);

            if (name.contains("ácido"))
                parsedInorganic.addSearchTagOf(name.replace("ácido ", ""));
        }
    }

    private String parseMolecularMass() {
        String molecularMass;

        int index = indexAfterIn("Masa molar:", htmlDocument);
        if (index == -1)
            index = indexAfterIn("Masa Molar:", htmlDocument);
        if (index == -1)
            index = indexAfterIn("Peso Molecular:", htmlDocument);

        if (index == -1)
            return null; // No molecular mass

        molecularMass = htmlDocument.substring(index + 1);
        molecularMass = molecularMass.substring(0, indexAfterIn("</", molecularMass) - 2);

        index = indexAfterIn("g", molecularMass);

        if (index == -1)
            return null; // No unit

        molecularMass = molecularMass.substring(0, index - 1);

        molecularMass = molecularMass.replace(" ", "").replace(",", ".");
        molecularMass = molecularMass.replace("a", "-"); // Otro formato de intervalo
        molecularMass = soloNumeros(molecularMass);
        molecularMass = primeroDelIntervalo(molecularMass);

        molecularMass = truncarDosDecimales(molecularMass);

        return molecularMass;
    }

    private String parseTemperature(String temperatureName) {
        String temperature;

        int index = indexAfterIn("Punto de " + temperatureName + ":", htmlDocument);
        if (index == -1)
            index = indexAfterIn("Temperatura de " + temperatureName + ":", htmlDocument);

        if (index == -1)
            return null; // No temperature of that kind

        temperature = htmlDocument.substring(index + 1);
        temperature = temperature.substring(0, indexAfterIn("</", temperature) - 2);

        index = indexAfterIn("°", temperature);
        if (index == -1)
            index = indexAfterIn("º", temperature); // Similar yet different character

        if (index == -1)
            return null; // No degree symbol

        temperature = temperature.substring(0, index - 1);

        temperature = temperature.replace(" ", "").replace(",", ".");
        temperature = temperature.replace("a", "-"); // Otro formato de intervalo
        temperature = soloNumeros(temperature);
        temperature = primeroDelIntervalo(temperature);
        temperature = String.valueOf(273.15 + Float.parseFloat(temperature));

        temperature = truncarDosDecimales(temperature);
        temperature = quitarCerosDecimalesALaDerecha(temperature);

        return temperature;
    }

    private String parseDensity() {
        String density;

        int indice = indexAfterIn("Densidad:", htmlDocument);
        if (indice == -1) // TODO code repeat
            return null; // No density

        density = htmlDocument.substring(indice + 1);
        density = density.substring(0, indexAfterIn("</", density) - 2);

        indice = indexAfterIn("g", density);
        if (indice == -1)
            return null; // No unit ("g", "Kg", "kg")

        boolean inKilograms;
        if (density.charAt(indice - 2) == 'k' || density.charAt(indice - 2) == 'K') {
            density = density.substring(0, indice - 2);
            inKilograms = true;
        } else {
            density = density.substring(0, indice - 1);
            inKilograms = false;
        }

        density = density.replace(" ", "").replace(",", ".");
        density = density.replace("a", "-"); // Otro formato de intervalo
        density = soloNumeros(density);
        density = primeroDelIntervalo(density);

        if (inKilograms) {
            float densityValue = Float.parseFloat(density);
            densityValue /= 1000; // From kg/m3 to g/cm3

            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setGroupingUsed(false);
            numberFormat.setMaximumFractionDigits(6);
            density = numberFormat.format(densityValue).replace(",", ".");

            density = tresDecimalesSignificativos(density);
        }

        density = quitarCerosDecimalesALaDerecha(density);

        return density;
    }

    // Utilities:

    private int indexAfterIn(String substring, String string) {
        int index = string.indexOf(substring);
        return (index != -1) ? index + substring.length() : index;
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

    // Getter:

    protected InorganicModel getParsedInorganic() {
        return parsedInorganic;
    }

}
