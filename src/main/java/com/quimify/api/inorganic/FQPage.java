package com.quimify.api.inorganic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.util.Map;

// This class parses inorganic compounds from FQ.com web pages.

class FQPage {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static Map<String, String> namingMistakeToCorrection = Map.of(
            "iuro", "uro", // I.e.: "antimoniuro", "arseniuro", "seleniuro" -> "antimonuro"...
            "iato", "ato", // I.e.: "arseniato", "antimoniato" -> "arsenato"...
            "teleruro", "telururo", // Common mistake
            "teleluro", "telururo", // Weird mistake in FQ.com
            "fosfonio", "fosfanio", // Common mistake
            "arsonio", "arsanio", // Common mistake
            "estibonio", "estibanio" // Common mistake
    );

    private InorganicModel parsedInorganic;

    private String htmlDocument;

    // Constructor:

    // Flowchart #5
    protected FQPage(String htmlDocument) {
        this.htmlDocument = htmlDocument;

        int index = indexAfterIn("<h1>", htmlDocument);

        if(index != -1) {
            this.htmlDocument = htmlDocument.substring(index); // Removes metadata

            parsedInorganic = new InorganicModel();

            parseAndSetFormula();
            parseAndSetNames();
            setSearchTags();
            fixNomenclatureMistakes(); // Might update search tags

            parseAndSetProperties();
        }
        else logger.error("La siguiente página de FQ no tiene <h1>: " + htmlDocument);
    }

    // Steps:

    private void parseAndSetFormula() {
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

        parsedInorganic.setFormula(formula);
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

        // It happens with some organic compounds:
        if(name.contains("br/>"))
            name = name.replace("br/>", "");

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

    private void fixNomenclatureMistakes() {
        correctPeroxide();
        correctNames();
    }

    private void correctPeroxide() {
        if (parsedInorganic.getSystematicName() != null && parsedInorganic.getSystematicName().contains("peróxido")) {
            parsedInorganic.setFormula(parsedInorganic.getFormula().replaceAll("O2*$", "(O2)")); // It's clearer
            parsedInorganic.setSystematicName(null); // Systematic names for peroxides NEVER include that word
            // TODO flag manual correction needed
        }
    }

    private void correctNames() {
        parsedInorganic.setStockName(correctName(parsedInorganic.getStockName()));
        parsedInorganic.setSystematicName(correctName(parsedInorganic.getSystematicName()));
        parsedInorganic.setTraditionalName(correctName(parsedInorganic.getTraditionalName()));
    }

    private String correctName(String name) {
        if(name == null)
            return null;

        name = name.replaceAll(" \\(", "("); // " (II)" -> "(II)", common mistake

        for(String namingMistake : namingMistakeToCorrection.keySet())
            if (name.contains(namingMistake)) {
                name = name.replace(namingMistake, namingMistakeToCorrection.get(namingMistake));
                setNameSearchTag(name);
            }

        return name;
    }

    private void parseAndSetProperties() {
        parsedInorganic.setMolecularMass(parseMolecularMass());
        parsedInorganic.setDensity(parseDensity());
        parsedInorganic.setMeltingPoint(parseTemperature("fusión"));
        parsedInorganic.setBoilingPoint(parseTemperature("ebullición"));
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
            return null; // Unit not found ("g")

        molecularMass = molecularMass.substring(0, index - 1); // TODO fix hardcoded 1

        molecularMass = molecularMass.replace(" ", "").replace(",", ".");
        molecularMass = molecularMass.replace("a", "-"); // Other interval format
        molecularMass = onlyDigits(molecularMass);
        molecularMass = firstInInterval(molecularMass);

        molecularMass = truncateToTwoDecimalPlaces(molecularMass); // Trailing zeros are kept

        return molecularMass;
    }

    private String parseTemperature(String temperatureLabel) {
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

    private String parseDensity() {
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

    // Getter:

    protected InorganicModel getParsedInorganic() {
        return parsedInorganic;
    }

}
