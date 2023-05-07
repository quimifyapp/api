package com.quimify.api.molecularmass;

import com.quimify.api.element.ElementModel;
import com.quimify.api.element.ElementService;
import com.quimify.api.error.ErrorService;
import com.quimify.api.metrics.MetricsService;
import com.quimify.api.notfoundquery.NotFoundQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Esta clase procesa las masas moleculares.

@Service
public
class MolecularMassService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ElementService elementService;

    @Autowired
    NotFoundQueryService notFoundQueryService;

    @Autowired
    ErrorService errorService;

    @Autowired
    MetricsService metricsService;

    // Internal:

    public Optional<Float> get(String formula) { // TODO just use tryCalculate?
        Optional<Float> molecularMass;

        try {
            MolecularMassResult molecularMassResult = calculate(formula);

            if(molecularMassResult.isPresent())
                molecularMass = Optional.ofNullable(molecularMassResult.getMolecularMass());
            else {
                String errorMessage = molecularMassResult.getError();
                errorService.log("Couldn't calculate: " + formula, errorMessage, getClass());
                molecularMass = Optional.empty();
            }
        } catch (StackOverflowError error) {
            errorService.log("StackOverflow error", formula, getClass());
            molecularMass = Optional.empty();
        } catch (Exception exception) {
            errorService.log("Exception calculating: " + formula, exception.toString(), getClass());
            molecularMass = Optional.empty();
        }

        return molecularMass;
    }

    // Client:

    MolecularMassResult tryCalculate(String query) {
        MolecularMassResult molecularMassResult;

        try {
            molecularMassResult = calculate(query);

            if (!molecularMassResult.isPresent())
                logger.warn("Couldn't calculate \"" + query + "\". " + "RETURN: " + molecularMassResult.getError());
        }
        catch (StackOverflowError error) {
            errorService.log("StackOverflow error", query, getClass());
            molecularMassResult = new MolecularMassResult("La fórmula es demasiado larga.");
        }
        catch(Exception exception) {
            errorService.log("Exception calculating: " + query, exception.toString(), getClass());
            molecularMassResult = new MolecularMassResult("");
        }

        if(!molecularMassResult.isPresent())
            notFoundQueryService.log(query, getClass());

        metricsService.molecularMassSearched(molecularMassResult.isPresent());

        return molecularMassResult;
    }

    // Private:

    private MolecularMassResult calculate(String formula) { // TODO translate code
        // Se comprueba si tiene aspecto de fórmula:
        String adapted = formula.replaceAll("[≡=-]", ""); // Bonds

        // TODO constant:
        Pattern structurePattern = Pattern.compile("(\\(*[A-Z][a-z]?(([2-9])|([1-9]\\d+))?" +
                "((\\(*)|(\\)(([2-9])|([1-9]\\d+))?))*)+"); // Once or more

        if (!structurePattern.matcher(adapted).matches())
            return new MolecularMassResult("La fórmula \"" + formula + "\" no es válida.");
        else if (StringUtils.countOccurrencesOf(adapted, "(") != StringUtils.countOccurrencesOf(adapted, ")"))
            return new MolecularMassResult("Los paréntesis no están balanceados.");
        else if (adapted.contains("()"))
            return new MolecularMassResult("Los paréntesis huecos \"()\" no son válidos.");

        // Parece que sí:

        Optional<Map<String, Integer>> elementToMoles = getElementToMolesIn(adapted); // Se analiza la fórmula

        // Se calcula la masa molecular:

        if (elementToMoles.isEmpty())
            return new MolecularMassResult("No se ha podido calcular la masa molecular.");

        float molecularMass = 0;
        Map<String, Float> elementToGrams = new HashMap<>();

        // Se calculan los gramos de cada elemento, siendo el total la masa molecular:

        for (Map.Entry<String, Integer> element : elementToMoles.get().entrySet()) {
            String symbol = element.getKey();

            Optional<Float> elementMolecularMass = getMolecularMassOf(symbol);

            if (elementMolecularMass.isEmpty())
                return new MolecularMassResult("No se reconoce el elemento \"" + symbol + "\".");

            float grams = element.getValue() * elementMolecularMass.get();
            elementToGrams.put(symbol, grams);

            molecularMass += grams;
        }

        return new MolecularMassResult(molecularMass, elementToGrams, elementToMoles.get());
    }

    private Optional<Map<String, Integer>> getElementToMolesIn(String formula) { // TODO translate and improve code
        Optional<Map<String, Integer>> resultado;

        // Separa las fórmulas anidadas entre paréntesis:

        int balance = 0;
        Map<String, Integer> anidada_a_moles = new HashMap<>();

        for(int i = 0, parentesis = 0; i < formula.length(); i++) {
            if(formula.charAt(i) == ')') {
                balance -= 1;

                if(balance == 0) { // Implica que hay '(' antes
                    StringBuilder anidada = new StringBuilder(formula.substring(parentesis + 1, i));

                    // Se procesan los cuantificadores:

                    i += 1; // Lo siguiente tras el último paréntesis

                    int moles;
                    if(i < formula.length()) {
                        String digitos = formula.substring(i);
                        Matcher matcher = Pattern.compile("^(\\d+)").matcher(digitos);
                        if (matcher.find()) {
                            digitos = matcher.group(1);
                            moles = digitos.length() > 0 ? Integer.parseInt(digitos) : 1;
                        }
                        else moles = 1;
                    }
                    else moles = 1;

                    addInMap(anidada.toString(), moles, anidada_a_moles); // Registra la fórmula anidada

                    // Finalmente:

                    String closedNested = "(" + anidada + ")" + (moles != 1 ? moles : "");
                    formula = formula.replaceFirst(Pattern.quote(closedNested), "");
                    i = parentesis - 1; // Continúa donde estaba el primer paréntesis (luego se incrementa en 1)
                }
                else if(balance < 0)
                    return Optional.empty();
            }
            else if(formula.charAt(i) == '(') {
                if(balance == 0)
                    parentesis = i;

                balance += 1;
            }
        }

        // Se suman las masas de las fórmulas anidadas (recursivo):

        Map<String, Integer> elemento_a_moles = new HashMap<>();

        for(Map.Entry<String, Integer> anidada : anidada_a_moles.entrySet()) {
            Optional<Map<String, Integer>> anidados = getElementToMolesIn(anidada.getKey());

            if(anidados.isPresent())
                for(Map.Entry<String, Integer> elemento : anidados.get().entrySet())
                    addInMap(elemento.getKey(), elemento.getValue() * anidada.getValue(), elemento_a_moles);
            else return Optional.empty();
        }

        // Procesa lo que no va entre paréntesis:

        if(balance == 0 && !(formula.contains("(") || formula.contains(")"))) { // No queda ningún paréntesis suelto
            if(!formula.equals("")) { // Puede pasar con fórmulas como "(NaCl)3" -> "()3"
                String[] partes = formula.split("(?=[A-Z])"); // "Aa11Bb22" -> ("Aa11", "Bb22")

                // Se registran los elementos y sus moles:

                for(String parte : partes) {
                    String simbolo = parte.replaceAll("\\d", "");
                    String digitos = parte.replaceAll("[A-Za-z]", "");
                    int moles = digitos.length() > 0 ? Integer.parseInt(digitos) : 1;

                    addInMap(simbolo, moles, elemento_a_moles); // Registra el elemento
                }
            }

            resultado = Optional.of(elemento_a_moles);
        }
        else resultado = Optional.empty();

        return resultado;
    }

    private void addInMap(String key, Integer value, Map<String, Integer> map) {
        Integer found = map.get(key);

        if(found != null)
            map.replace(key, found + value); // It was present, values are added
        else map.put(key, value); // New element
    }

    private Optional<Float> getMolecularMassOf(String symbol) {
        return elementService.searchBySymbol(symbol).map(ElementModel::getMolecularMass);
    }

}
