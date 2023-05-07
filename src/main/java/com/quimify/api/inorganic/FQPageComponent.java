package com.quimify.api.inorganic;

import com.quimify.api.utils.Connection;
import com.quimify.api.error.ErrorService;
import com.quimify.api.settings.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// This class parses inorganic compounds from FQ.com web pages.

@Component
@Scope("prototype") // New instance for each request, avoiding shared state
class FQPageComponent {

    @Autowired
    SettingsService settingsService;

    @Autowired
    ErrorService errorService;

    String htmlDocument;
    InorganicModel parsedInorganic;

    // Constants:

    private final static String fqUrl = "https://www.formulacionquimica.com/";

    private final static Set<String> invalidSubdirectories = Set.of(
            ".com/", "acidos-carboxilicos/", "alcanos/", "alcoholes/", "aldehidos/", "alquenos/", "alquinos/",
            "amidas/", "aminas/", "anhidridos/", "anhidridos-organicos/", "aromaticos/", "buscador/", "cetonas/",
            "cicloalquenos/", "ejemplos/", "ejercicios/", "esteres/", "eteres/", "halogenuros/", "hidracidos/",
            "hidroxidos/", "hidruros/", "hidruros-volatiles/", "inorganica/", "nitrilos/", "organica/",
            "oxidos-metalicos/", "oxidos/", "oxisales/", "oxoacidos/", "peroxidos/", "politica-privacidad/",
            "sales-neutras/", "sales-volatiles/"
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

    // Protected:

    InorganicModel parseInorganic(String url) throws IOException {
        if (!url.contains(fqUrl))
            throw new IllegalArgumentException("Not a FQ address.");

        if (invalidSubdirectories.stream().anyMatch(url::endsWith))
            throw new IllegalArgumentException("Invalid subdirectory.");

        Connection connection = new Connection(url);
        connection.setProperty("User-Agent", settingsService.getUserAgent());

        htmlDocument = connection.getText();

        parsedInorganic = new InorganicModel();

        parseAndSetFormula();
        parseAndSetNames();
        parseAndSetProperties();

        setSearchTags();
        fixNomenclatureMistakes(); // Might update search tags

        return parsedInorganic;
    }

    // Steps:

    private void parseAndSetFormula() {
        String formula;

        Optional<Integer> headerIndex = indexAfterIn("<h1>", htmlDocument);

        if (headerIndex.isEmpty())
            throw new IllegalStateException("Couldn't find header in HTML document.");

        String header = htmlDocument.substring(headerIndex.get());

        Optional<Integer> slashIndex = indexAfterIn("/", header);
        Optional<Integer> closingTagIndex = indexAfterIn("</", header);

        if (slashIndex.equals(closingTagIndex)) { // "metano</h1>..."
            slashIndex = indexAfterIn(">Fórmula:", header);

            if (slashIndex.isEmpty())
                slashIndex = indexAfterIn("\"frm\">", header);

            formula = header.substring(slashIndex.get()); // TODO optional get check
            formula = formula.substring(0, formula.indexOf("</p>"));

            formula = formula.replaceAll("(</?sub>)|(</b>)| ", ""); // <sub> or </sub> or </b> or ' '
        }
        else formula = header.substring(0, slashIndex.get() - 2); // "Co2(CO3)3 / carbonato de cobalto(III)</h1>"
        // TODO optional get check
        parsedInorganic.setFormula(formula);
    }

    private void parseAndSetNames() {
        Optional<Integer> index = indexAfterIn("sistemática:</b>", htmlDocument);
        parsedInorganic.setSystematicName(parseName(index));

        index = indexAfterIn("stock:</b>", htmlDocument);
        parsedInorganic.setStockName(parseName(index));

        index = indexAfterIn("tradicional:</b>", htmlDocument);
        parsedInorganic.setTraditionalName(parseName(index));
    }

    private String parseName(Optional<Integer> index) {
        if (index.isEmpty())
            return null;

        String name = htmlDocument.substring(index.get() + 1);
        name = name.substring(0, name.indexOf("</p>"));

        // It happens with some organic compounds:
        if (name.contains("br/>"))
            name = name.replace("br/>", "");

        return name;
    }

    private void setSearchTags() {
        setNameSearchTag(parsedInorganic.getStockName());
        setNameSearchTag(parsedInorganic.getSystematicName());
        setNameSearchTag(parsedInorganic.getTraditionalName());
    }

    private void setNameSearchTag(String name) {
        if (name != null && name.contains("ácido")) // TODO "zinc", in corrections not in search tags
            parsedInorganic.addSearchTag(name.replace("ácido ", ""));
    }

    private void fixNomenclatureMistakes() {
        if (parsedInorganic.toString().contains("peróxido")) // Any of its names
            correctPeroxide();

        parsedInorganic.setStockName(correctName(parsedInorganic.getStockName()));
        parsedInorganic.setSystematicName(correctName(parsedInorganic.getSystematicName()));
        parsedInorganic.setTraditionalName(correctName(parsedInorganic.getTraditionalName()));
    }

    private void correctPeroxide() {
        parsedInorganic.setFormula(parsedInorganic.getFormula().replaceAll("O2*$", "(O2)")); // It's clearer
        parsedInorganic.setSystematicName(null); // Systematic names for peroxides NEVER include that word

        errorService.log("Learned peroxide needs manual correction", parsedInorganic.toString(), getClass());
    }

    private String correctName(String name) {
        if (name == null)
            return null;

        // Uppercase if not between parentheses, so oxidation numbers like "(IV)" stay uppercase:
        name = Pattern.compile(".(?![^(]*\\))").matcher(name).replaceAll(match -> match.group().toLowerCase());

        for (String namingMistake : namingMistakeToCorrection.keySet())
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
        Optional<String> characteristic = parseCharacteristic(
                List.of("Masa molar", "Masa Molar", "Peso Molecular"),
                List.of("g")
        );

        if (characteristic.isEmpty())
            return null;

        return truncateToTwoDecimalPlaces(characteristic.get()); // Trailing zeros are kept
    }

    private String parseTemperature(String temperatureLabel) {
        Optional<String> parsedCharacteristic = parseCharacteristic(
                List.of("Punto de " + temperatureLabel, "Temperatura de " + temperatureLabel),
                List.of("°", "º") // Similar yet different character
        );

        if (parsedCharacteristic.isEmpty())
            return null;

        String temperature = String.valueOf(273.15 + Float.parseFloat(parsedCharacteristic.get()));

        temperature = truncateToTwoDecimalPlaces(temperature);
        temperature = removeTrailingZeros(temperature);

        temperature = temperature.replaceAll("\\.15$", ""); // It didn't include decimals in Celsius

        return temperature;
    }

    private Optional<String> parseCharacteristic(List<String> labels, List<String> units) {
        String characteristic;

        Optional<Integer> indexAfterLabel = Optional.empty();

        for (String label : labels) {
            indexAfterLabel = indexAfterIn(label + ":", htmlDocument);

            if (indexAfterLabel.isPresent())
                break;
        }

        if (indexAfterLabel.isEmpty())
            return Optional.empty();

        characteristic = htmlDocument.substring(indexAfterLabel.get() + 1);
        characteristic = characteristic.substring(0, characteristic.indexOf("</"));

        int unitIndex = -1;

        for (String unit : units) {
            unitIndex = characteristic.indexOf(unit);

            if (unitIndex != -1)
                break;
        }

        if (unitIndex == -1)
            return Optional.empty();

        characteristic = characteristic.substring(0, unitIndex);
        characteristic = formatCharacteristic(characteristic);

        return Optional.of(characteristic);
    }

    private String parseDensity() {
        String density;

        Optional<Integer> index = indexAfterIn("Densidad:", htmlDocument);

        if (index.isEmpty())
            return null; // No density

        density = htmlDocument.substring(index.get() + 1);
        density = density.substring(0, density.indexOf("</"));

        int unitIndex = density.indexOf("kg");

        if (unitIndex == -1)
            unitIndex = density.indexOf("Kg");

        boolean inKilograms = unitIndex != -1;

        if (!inKilograms) {
            unitIndex = density.indexOf("g");

            if (unitIndex == -1)
                return null; // Unit not found ("g", "Kg", "kg")
        }

        density = density.substring(0, unitIndex);
        density = formatCharacteristic(density);

        if (inKilograms) {
            float densityValue = Float.parseFloat(density);
            densityValue /= 1000; // kg/m3 -> g/cm3

            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setGroupingUsed(false);
            numberFormat.setMaximumFractionDigits(6);
            density = numberFormat.format(densityValue).replace(",", ".");

            density = truncateToThreeSignificantDecimalPlaces(density);
        }

        density = removeTrailingZeros(density);

        return density;
    }

    private String formatCharacteristic(String characteristic) {
        characteristic = characteristic.replace(" ", "");
        characteristic = characteristic.replace(",", ".");
        characteristic = characteristic.replace("a", "-"); // Other interval format
        characteristic = onlyDigits(characteristic);
        return firstInInterval(characteristic);
    }

    // Utilities:

    // I.e.: "-1,104 ºC - 0.34 " -> "-1,104-0.34"
    private String onlyDigits(String data) {
        return data.replaceAll("/[^\\d-.]", ""); // Dashes and decimal points too
    }

    // I.e.: "-12.104 - 13.27" -> "12.104"
    // I.e.: "-12.104" -> "12.104"
    private String firstInInterval(String interval) {
        interval = interval.trim(); // Leading and trailing spaces removed
        Matcher matcher = Pattern.compile("^-?[^-]+").matcher(interval); // First dash might be a negative symbol
        return matcher.find() ? matcher.group() : "";
    }

    // I.e.: "14.457 -> 14.45"
    private String truncateToTwoDecimalPlaces(String number) {
        Optional<Integer> decimalPointIndex = indexAfterIn(".", number);

        if (decimalPointIndex.isEmpty())
            return number;

        int afterDecimalsIndex = decimalPointIndex.get() + 2;

        if (number.length() > afterDecimalsIndex) // There are more than two decimals places
            number = number.substring(0, afterDecimalsIndex);

        return number;

        // TODO Regexp?
        // Matcher matcher = Pattern.compile("^[^.]+\\.(\\d{0,2}).*$").matcher(number);
        // return matcher.find() ? matcher.replaceFirst("$1") : number;
    }

    // I.e.: "13.450" -> "13.45"
    // I.e.: "6.0" -> "6"
    private String removeTrailingZeros(String number) {
        return number.replaceAll("\\.?0+$", ""); // Possible dangling point too
    }

    // I.e.: "X.000ABCD" -> "X.000ABC"
    private String truncateToThreeSignificantDecimalPlaces(String number) {
        Optional<Integer> decimalPointIndex = indexAfterIn(".", number);

        if (decimalPointIndex.isEmpty())
            return number;

        for (int i = decimalPointIndex.get(), significantCount = 0; i < number.length() && significantCount < 3; i++)
            if (number.charAt(i) != '0')
                if (++significantCount == 3)
                    number = number.substring(0, i + 1);

        return number;

        // TODO Regexp?
        // Matcher matcher = Pattern.compile("^([^.]+\\.[0]{0,3}[1-9]+).*$").matcher(number);
        // return matcher.find() ? matcher.replaceFirst("$1") : number;
    }

    private Optional<Integer> indexAfterIn(String substring, String string) {
        int index = string.indexOf(substring);

        return index != -1
                ? Optional.of(index + substring.length())
                : Optional.empty();
    }

}
