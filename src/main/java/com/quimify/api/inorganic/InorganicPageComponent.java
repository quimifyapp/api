package com.quimify.api.inorganic;

import com.quimify.api.error.ErrorService;
import com.quimify.utils.Download;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Set;

// This class parses inorganic compounds from FQ.com web pages.

@Component
class InorganicPageComponent {

    @Autowired
    ErrorService errorService; // API errors logic

    private final static String fqUrl = "https://www.formulacionquimica.com/";

    private final static Set<String> invalidSubdirectories = Set.of(
            ".com/", "acidos-carboxilicos/", "alcanos/", "alcoholes/", "aldehidos/", "alquenos/", "alquinos/",
            "amidas/", "aminas/", "anhidridos/", "anhidridos-organicos/", "aromaticos/", "buscador/", "cetonas/",
            "cicloalquenos/", "ejemplos/", "ejercicios/", "esteres/", "eteres/", "halogenuros/", "hidracidos/",
            "hidroxidos/", "hidruros/", "hidruros-volatiles/", "inorganica/", "nitrilos/", "organica/", "oxidos/",
            "oxisales/", "oxoacidos/", "peroxidos/", "politica-privacidad/", "sales-neutras/", "sales-volatiles/"
    );

    private final static Map<String, String> namingMistakeToCorrection = Map.of(
            " (", "(", // I.e.: "hierro (II)" -> "hierro(II)"
            "iuro", "uro", // I.e.: "antimoniuro", "arseniuro", "seleniuro" -> "antimonuro"...
            "iato", "ato", // I.e.: "arseniato", "antimoniato" -> "arsenato"...
            "teleruro", "telururo", // Common mistake
            "teleluro", "telururo", // Weird mistake in FQ.com
            "fosfonio", "fosfanio", // Common mistake
            "arsonio", "arsanio", // Common mistake
            "estibonio", "estibanio" // Common mistake
    );

    protected InorganicModel parseInorganic(String url, String userAgent) throws IOException {
        if(!url.contains(fqUrl))
            throw new IllegalArgumentException("Not a FQ address.");

        if(invalidSubdirectories.stream().anyMatch(url::endsWith))
            throw new IllegalArgumentException("Invalid subdirectory.");

        Download connection = new Download(url);
        connection.setProperty("User-Agent", userAgent);

        String htmlDocument = connection.getText();

        int index = indexAfterIn("<h1>", htmlDocument);

        if (index == -1)
            throw new IllegalStateException("Couldn't find <h1> tag in HTML document.");

        htmlDocument = htmlDocument.substring(index); // Removes metadata

        InorganicModel parsedInorganic = new InorganicModel();

        parseAndSetFormula(parsedInorganic, htmlDocument);
        parseAndSetNames(parsedInorganic, htmlDocument);
        parseAndSetProperties(parsedInorganic, htmlDocument);

        setSearchTags(parsedInorganic);
        fixNomenclatureMistakes(parsedInorganic); // Might update search tags

        return parsedInorganic;
    }

    // Steps:

    private void parseAndSetFormula(InorganicModel inorganicModel, String htmlDocument) {
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
        }
        else formula = htmlDocument.substring(0, index - 2); // "Co2(CO3)3 / carbonato de cobalto (III)</h1>..."

        inorganicModel.setFormula(formula);
    }

    private void parseAndSetNames(InorganicModel inorganicModel, String htmlDocument) {
        int index = indexAfterIn("sistemática:</b>", htmlDocument);
        inorganicModel.setSystematicName(parseName(htmlDocument, index));

        index = indexAfterIn("stock:</b>", htmlDocument);
        inorganicModel.setStockName(parseName(htmlDocument, index));

        index = indexAfterIn("tradicional:</b>", htmlDocument);
        inorganicModel.setTraditionalName(parseName(htmlDocument, index));
    }

    private String parseName(String htmlDocument, int index) {
        if (index == -1)
            return null;

        String name = htmlDocument.substring(index + 1);
        name = name.substring(0, indexAfterIn("</p>", name) - 4);

        // It happens with some organic compounds:
        if(name.contains("br/>"))
            name = name.replace("br/>", "");

        return name;
    }

    private void setSearchTags(InorganicModel inorganicModel) {
        inorganicModel.addSearchTagOf(inorganicModel.getFormula());

        setNameSearchTag(inorganicModel, inorganicModel.getStockName());
        setNameSearchTag(inorganicModel, inorganicModel.getSystematicName());
        setNameSearchTag(inorganicModel, inorganicModel.getTraditionalName());
    }

    private void setNameSearchTag(InorganicModel inorganicModel, String name) {
        if(name != null) {
            inorganicModel.addSearchTagOf(name);

            if (name.contains("ácido"))
                inorganicModel.addSearchTagOf(name.replace("ácido ", ""));
        }
    }

    private void fixNomenclatureMistakes(InorganicModel inorganicModel) {
        if (inorganicModel.toString().contains("peróxido")) // Any of its names
            correctPeroxide(inorganicModel);

        correctNames(inorganicModel);
    }

    private void correctPeroxide(InorganicModel inorganicModel) {
        inorganicModel.setFormula(inorganicModel.getFormula().replaceAll("O2*$", "(O2)")); // It's clearer
        inorganicModel.setSystematicName(null); // Systematic names for peroxides NEVER include that word

        errorService.saveError("Peroxide needs manual correction", inorganicModel.toString(), this.getClass());
    }

    private void correctNames(InorganicModel inorganicModel) {
        inorganicModel.setStockName(correctName(inorganicModel, inorganicModel.getStockName()));
        inorganicModel.setSystematicName(correctName(inorganicModel, inorganicModel.getSystematicName()));
        inorganicModel.setTraditionalName(correctName(inorganicModel, inorganicModel.getTraditionalName()));
    }

    private String correctName(InorganicModel inorganicModel, String name) {
        if(name == null)
            return null;

        // TODO uppercase but not between parentheses

        for(String namingMistake : namingMistakeToCorrection.keySet())
            if (name.contains(namingMistake)) {
                name = name.replace(namingMistake, namingMistakeToCorrection.get(namingMistake));
                setNameSearchTag(inorganicModel, name);
            }

        return name;
    }

    private void parseAndSetProperties(InorganicModel inorganicModel, String htmlDocument) {
        inorganicModel.setMolecularMass(parseMolecularMass(htmlDocument));
        inorganicModel.setDensity(parseDensity(htmlDocument));
        inorganicModel.setMeltingPoint(parseTemperature(htmlDocument, "fusión"));
        inorganicModel.setBoilingPoint(parseTemperature(htmlDocument, "ebullición"));
    }

    private String parseMolecularMass(String htmlDocument) {
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
            return null; // Unit not found ("g")

        molecularMass = molecularMass.substring(0, index - 1); // TODO fix hardcoded 1

        molecularMass = molecularMass.replace(" ", "").replace(",", ".");
        molecularMass = molecularMass.replace("a", "-"); // Other interval format
        molecularMass = onlyDigits(molecularMass);
        molecularMass = firstInInterval(molecularMass);

        molecularMass = truncateToTwoDecimalPlaces(molecularMass); // Trailing zeros are kept

        return molecularMass;
    }

    private String parseTemperature(String htmlDocument, String temperatureLabel) {
        String temperature;

        int index = indexAfterIn("Punto de " + temperatureLabel + ":", htmlDocument);
        if (index == -1)
            index = indexAfterIn("Temperatura de " + temperatureLabel + ":", htmlDocument);

        if (index == -1)
            return null; // No temperature of that kind

        temperature = htmlDocument.substring(index + 1);
        temperature = temperature.substring(0, indexAfterIn("</", temperature) - 2);

        index = indexAfterIn("°", temperature);
        if (index == -1)
            index = indexAfterIn("º", temperature); // Similar yet different character

        if (index == -1) // TODO fix repeated code from here?
            return null; // Unit not found ("°C" or "ºC", not identical)

        temperature = temperature.substring(0, index - 1); // TODO fix hardcoded 1

        temperature = temperature.replace(" ", "").replace(",", ".");
        temperature = temperature.replace("a", "-"); // Other interval format
        temperature = onlyDigits(temperature);
        temperature = firstInInterval(temperature);
        temperature = String.valueOf(273.15 + Float.parseFloat(temperature));

        temperature = truncateToTwoDecimalPlaces(temperature);
        temperature = removeTrailingZeros(temperature);

        temperature = temperature.replaceAll("\\.15$", ""); // It didn't include decimals in Celsius

        return temperature;
    }

    private String parseDensity(String htmlDocument) {
        String density;

        int indice = indexAfterIn("Densidad:", htmlDocument);

        if (indice == -1)
            return null; // No density

        density = htmlDocument.substring(indice + 1);
        density = density.substring(0, indexAfterIn("</", density) - 2);

        indice = indexAfterIn("g", density);

        if (indice == -1)
            return null; // Unit not found ("g", "Kg", "kg")

        boolean inKilograms;
        if (density.charAt(indice - 2) == 'k' || density.charAt(indice - 2) == 'K') {
            density = density.substring(0, indice - 2); // TODO fix hardcoded 2
            inKilograms = true;
        } else {
            density = density.substring(0, indice - 1); // TODO fix hardcoded 1
            inKilograms = false;
        }

        density = density.replace(" ", "").replace(",", ".");
        density = density.replace("a", "-"); // Other interval format
        density = onlyDigits(density);
        density = firstInInterval(density);

        if (inKilograms) {
            float densityValue = Float.parseFloat(density);
            densityValue /= 1000; // From kg/m3 to g/cm3

            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setGroupingUsed(false);
            numberFormat.setMaximumFractionDigits(6);
            density = numberFormat.format(densityValue).replace(",", ".");

            density = truncateToThreeSignificantDecimalPlaces(density);
        }

        density = removeTrailingZeros(density);

        return density;
    }

    // Utilities:

    private int indexAfterIn(String substring, String string) {
        int index = string.indexOf(substring);
        return index != -1 ? index + substring.length() : index;
    }

    // I.e.: "-1,104 ºC - 0.34 " -> "-1,104-0.34"
    private String onlyDigits(String data) {
        // Dashes and decimal points too
        return data.replaceAll("/[^\\d-.]", "");
    }

    // I.e.: "-12.104 - 13.27" -> "12.104"
    // I.e.: "-12.104" -> "12.104"
    private String firstInInterval(String interval) {
        interval = interval.trim(); // Leading and trailing spaces removed

        if(interval.substring(1).contains("-")) // First dash might be a negative symbol
            interval = interval.split("-", 2)[0];

        return interval;
    }

    // I.e.: "14.457 -> 14.45"
    private String truncateToTwoDecimalPlaces(String number) {
        int decimalPointIndex = indexAfterIn(".", number);

        if (decimalPointIndex == -1)
            return number;

        decimalPointIndex += 2;

        if(number.length() > decimalPointIndex) // There are more than two decimals places
            number = number.substring(0, decimalPointIndex);

        return number;
    }

    // I.e.: "13.450" -> "13.45"
    // I.e.: "6.0" -> "6"
    private String removeTrailingZeros(String number) {
        return number.replaceAll("\\.?0+$", ""); // Possible dangling point too
    }

    // I.e.: "X.000ABCD" -> "X.000ABC"
    private String truncateToThreeSignificantDecimalPlaces(String number) {
        int decimalPointIndex = indexAfterIn(".", number);

        if (decimalPointIndex == -1)
            return number;

        for(int i = decimalPointIndex, significantCount = 0; i < number.length() && significantCount < 3; i++)
            if(number.charAt(i) != '0')
                if(++significantCount == 3)
                    number = number.substring(0, i + 1);

        return number;
    }

}
