package com.quimify.api.molecular_mass;

import com.quimify.api.element.ElementModel;
import com.quimify.api.element.ElementService;
import com.quimify.api.metrics.MetricsService;
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ElementService elementService; // Procesos de los elementos

    @Autowired
    MetricsService metricsService; // Procesos de las metricas diarias

    // -------------------------------------------------------------------------------

    public Float tryMolecularMassOf(String formula) {
        return tryMolecularMassResultOf(formula).getMolecularMass();
    }

    protected MolecularMassResult tryMolecularMassResultOf(String formula) {
        MolecularMassResult molecularMassResult;

        try {
            molecularMassResult = calculateMolecularMassOf(formula);

            if (!molecularMassResult.getPresent())
                logger.warn("No se ha podido calcular la masa de \"" + formula + "\". " +
                        "Error: " + molecularMassResult.getError());
        }
        catch (StackOverflowError error) {
            molecularMassResult = new MolecularMassResult("La fórmula es demasiado larga.");
            logger.warn("StackOverflow error al calcular la masa de: \"" + formula + "\".");
        }
        catch(Exception exception) {
            molecularMassResult = new MolecularMassResult("");
            logger.error("Excepción al calcular la masa de \"" + formula + "\": " + exception);
        }

        metricsService.contarMasaMolecular(molecularMassResult.getPresent());

        return molecularMassResult;
    }
    
    // INTERNOS ----------------------------------------------------------------------
    
    private MolecularMassResult calculateMolecularMassOf(String formula) {
        // Se comprueba si tiene aspecto de fórmula:
        String adapted = formula.replaceAll("[≡=-]", ""); // Allowed bonds

        Pattern structurePattern = Pattern.compile("(\\(*[A-Z][a-z]?(([2-9])|([1-9]\\d+))?" +
                "((\\(*)|(\\)(([2-9])|([1-9]\\d+))?))*)+"); // Once or more

        if(!structurePattern.matcher(adapted).matches())
            return new MolecularMassResult("La fórmula \"" + formula + "\" no es válida.");
        else if(StringUtils.countOccurrencesOf(adapted, "(") != StringUtils.countOccurrencesOf(adapted, ")"))
            return new MolecularMassResult("Los paréntesis no están balanceados.");
        else if(adapted.contains("()"))
            return new MolecularMassResult("Los paréntesis huecos \"()\" no son válidos.");

        // Parece que sí:

        MolecularMassResult resultado;

        Optional<Map<String, Integer>> elemento_a_moles = getElementToMolesIn(adapted); // Se analiza la fórmula

        // Se calcula la masa molecular:

        if(elemento_a_moles.isPresent()) {
            float masa_molecular = 0;
            Map<String, Float> elemento_a_gramos = new HashMap<>();

            // Se calculan los gramos de cada elemento, siendo el total la masa molecular:

            for(Map.Entry<String, Integer> elemento : elemento_a_moles.get().entrySet()) {
                String simbolo = elemento.getKey();

                Optional<Float> masa_elemento = getMolecularMassOf(simbolo);

                if(masa_elemento.isPresent()) {
                    float gramos = elemento.getValue() * masa_elemento.get();
                    elemento_a_gramos.put(simbolo, gramos);

                    masa_molecular += gramos;
                }
                else return new MolecularMassResult("No se reconoce el elemento \"" + simbolo + "\".");
            }

            resultado = new MolecularMassResult(masa_molecular, elemento_a_gramos, elemento_a_moles.get());
        }
        else return new MolecularMassResult("No se ha podido calcular la masa molecular.");

        return resultado;
    }

    private Optional<Map<String, Integer>> getElementToMolesIn(String formula) {
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
