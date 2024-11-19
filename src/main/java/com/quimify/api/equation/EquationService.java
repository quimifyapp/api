package com.quimify.api.equation;

import com.quimify.api.error.ErrorService;

import java.util.*;
import java.util.stream.Collectors;

import com.quimify.api.metrics.MetricsService;
import com.quimify.api.notfoundquery.NotFoundQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// TODO rename Equation to Reaction

@Service
class EquationService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    NotFoundQueryService notFoundQueryService;

    @Autowired
    ErrorService errorService;

    @Autowired
    MetricsService metricsService;

    EquationResult tryBalance(String reactants, String products) {
        EquationResult equationResult;

        String equation = reactants + " = " + products;

        try {
            equationResult = balance(reactants, products);

            if (!equationResult.isPresent())
                logger.warn("Couldn't calculate \"" + equation + "\". " + "RESULT: " + equationResult.getError());
        } catch (StackOverflowError error) {
            errorService.log("StackOverflow error", equation, getClass());
            equationResult = EquationResult.error("La reacción es demasiado larga.");
        } catch (Exception exception) {
            errorService.log("Exception calculating: " + equation, exception.toString(), getClass());
            equationResult = EquationResult.notPresent();
        }

        if (!equationResult.isPresent())
            notFoundQueryService.log(equation, getClass());

        metricsService.balanceEquationQueried(equationResult.isPresent());

        return equationResult;
    }

    // Private:

    private EquationResult balance(String reactantsText, String productsText) {
        reactantsText = removeUnnecessaryCharacters(reactantsText);
        productsText = removeUnnecessaryCharacters(productsText);

        Set<String> reactantsElements = getElementsInSumOfFormulas(reactantsText);
        Set<String> productsElements = getElementsInSumOfFormulas(productsText);

        if (!reactantsElements.equals(productsElements))
            return EquationResult.error("Deben aparecer los mismos elementos en ambas partes de la reacción.");

        List<Formula> reactants = getFormulasInSumOfFormulas(reactantsText);
        List<Formula> products = getFormulasInSumOfFormulas(productsText);
        int[] solution = balance(reactants, products, reactantsElements);

        if (notBalanceable(solution))
            return EquationResult.error("La reacción no es balanceable.");

        int[] reactantsSolution = Arrays.copyOfRange(solution, 0, reactants.size());
        int[] productsSolution = Arrays.copyOfRange(solution, reactants.size(), solution.length);

        String resultReactants = formatSolution(reactantsText, reactantsSolution);
        String resultProducts = formatSolution(productsText, productsSolution);

        return new EquationResult(resultReactants, resultProducts);
    }

    private int[] balance(List<Formula> reactants, List<Formula> products, Set<String> element) {
        Matrix equations = getReactionEquations(reactants, products, element);

        Fraction[] anySolution = Mathematics.calculateAnySolution(equations);
        int[] integerSolution = Mathematics.rescaleIntoIntegers(anySolution);

        return Mathematics.findMinimalSolution(equations, integerSolution);
    }

    private Matrix getReactionEquations(List<Formula> reactants, List<Formula> products, Set<String> elements) {
        Matrix equations = new Matrix(elements.size(), reactants.size() + products.size() + 1);

        Iterator<String> elementsIterator = elements.iterator();

        for (int row = 0; row < elements.size(); row++) {
            String element = elementsIterator.next();

            for (int column = 0; column < reactants.size(); column++) {
                Formula reactant = reactants.get(column);
                int molesOfElementInReactant = reactant.molesOf(element);

                equations.set(row, column, new Fraction(molesOfElementInReactant));
            }

            for (int column = reactants.size(); column < equations.columns() - 1; column++) {
                Formula product = products.get(column - reactants.size());
                int molesOfElementInProduct = -1 * product.molesOf(element);

                equations.set(row, column, new Fraction(molesOfElementInProduct));
            }
        }

        return equations;
    }

    private boolean notBalanceable(int[] solution) {
        return Arrays.stream(solution).anyMatch(element -> element == 0);
    }

    // TODO clean this code from the internet:
        // "Gets all elements used on a side an equation"
    private static Set<String> getElementsInSumOfFormulas(String sumOfFormulas) {
        LinkedHashSet<String> elements = new LinkedHashSet<>();
        String elementString = "";
        char character = 0;
        for (int i = 0; i < sumOfFormulas.length(); i++) {
            character = sumOfFormulas.charAt(i);
            if (Character.isLetter(character)) {
                if (String.valueOf(character).toUpperCase().equals(String.valueOf(character))) {
                    if (!elementString.isEmpty()) {
                        elements.add(elementString);
                        elementString = "";
                    }
                }
                elementString = elementString.concat(Character.toString(character));
            } else if (Character.toString(character).equals("+")) {
                elements.add(elementString);
                elementString = "";
            }
        }
        if (!Character.toString(character).isEmpty()) {
            elements.add(elementString);
        }

        return elements;
    }

    private List<Formula> getFormulasInSumOfFormulas(String sumOfFormulas) {
        return Arrays.stream(sumOfFormulas.split("\\+")).map(Formula::new).collect(Collectors.toList());
    }

    // TODO clean this code from the internet:
        // "Removes all characters that are not letters, numbers, parentheses(), and plus signs("+")"
    private static String removeUnnecessaryCharacters(String currentString) {
        currentString = currentString.replaceAll("\\s", "");
        currentString = currentString.replaceAll("\\++", "+");
        currentString = currentString.replaceAll("^\\++|\\++$", "");
        currentString = currentString.replaceAll("[^a-zA-Z0-9()+=]", "");

        // Stack to hold the index and content status of unmatched opening parentheses
        Stack<Integer> stack = new Stack<>();
        StringBuilder sb = new StringBuilder();
        boolean[] hasContent = new boolean[currentString.length()];  // Tracks content between parentheses

        for (int i = 0; i < currentString.length(); i++) {
            char ch = currentString.charAt(i);
            if (ch == '(') {
                // Push the current length (position of '(') onto the stack and assume no content initially
                stack.push(sb.length());
                hasContent[sb.length()] = false;  // Default to no content
                sb.append(ch);
            } else if (ch == ')') {
                if (stack.isEmpty()) {
                    // There's a closing parenthesis without a matching opening, ignore it
                    continue;
                }
                // Check if there was content
                int openPos = stack.pop();
                if (!hasContent[openPos]) {
                    // No content between the parentheses, remove the opening parenthesis
                    sb.deleteCharAt(openPos);
                } else {
                    // Valid content found, append closing parenthesis
                    sb.append(ch);
                }
            } else {
                // Append other characters directly
                sb.append(ch);
                if (!stack.isEmpty()) {
                    // Mark that there has been content since the last '('
                    hasContent[stack.peek()] = true;
                }
            }
        }

        // Any remaining '(' in the stack are unmatched, remove them
        while (!stack.isEmpty()) {
            sb.deleteCharAt(stack.pop());
        }

        return sb.toString();
    }

    // TODO clean this code from the internet
    private static String formatSolution(String originalString, int[] solutions) {
        String[] arr = originalString.split("\\+");
        StringBuilder s = new StringBuilder();
        String coefficientHandler = "";

        for (int i = 0; i < solutions.length; i++) {
            if (Character.isDigit(arr[i].charAt(0))) {
                int j = 0;
                while (Character.isDigit(arr[i].charAt(j))) {
                    coefficientHandler = coefficientHandler.concat(String.valueOf(arr[i].charAt(j)));
                    j++;
                }
                arr[i] = arr[i].substring(j);
                int newCoefficient = Integer.parseInt(coefficientHandler) * solutions[i];

                if (newCoefficient != 1) { // Only append if coefficient is not 1
                    s.append(newCoefficient);
                }

                appendElement(arr, i, s, solutions, newCoefficient);

                coefficientHandler = "";
            } else {
                if (solutions[i] != 1) { // Only append if coefficient is not 1
                    s.append(solutions[i]);
                }

                appendElement(arr, i, s, solutions, solutions[i]);

            }
        }
        return s.toString();
    }

    // TODO clean this code from the internet:
    private static void appendElement(String[] arr, int i, StringBuilder s,  int[] solutions, int newCoefficient) {

        if (arr[i].length() == 2 && (Character.isDigit(arr[i].charAt(1)) || Character.isLowerCase(arr[i].charAt(1))))
            s.append(arr[i]);
        else if (arr[i].length() == 3 && Character.isDigit(arr[i].charAt(2)) && Character.isLowerCase(arr[i].charAt(1)))
            s.append(arr[i]);
        else {
            if (arr[i].length() > 1 && newCoefficient != 1)
                s.append('(');

            s.append(arr[i]);

            if (arr[i].length() > 1 && newCoefficient != 1)
                s.append(')');
        }

        if (i < solutions.length - 1)
            s.append(" + ");
    }

    // TODO remove:
    String testTemporal() {
        // 1
        if (!balance("2H + O", "H3O").getBalancedEquation().equals("6H + 2O = 2(H3O)"))
            return "2H + O = H3O";
        // 2
        if (!balance("H3PO4 + ___ Mg(OH)2", "___ Mg3(PO4)2 + ___ H2O").getBalancedEquation().equals("2(H3PO4) + 3(Mg(OH)2) = Mg3(PO4)2 + 6(H2O)"))
            return "H3PO4 + ___ Mg(OH)2 = ___ Mg3(PO4)2 + ___ H2O";
        // 3
        if (!balance("Al (OH)3 + ___ H2CO3", "___ Al2(CO3)3 + ___ H2O").getBalancedEquation().equals("2(Al(OH)3) + 3(H2CO3) = Al2(CO3)3 + 6(H2O)"))
            return "Al (OH)3 + ___ H2CO3 = ___ Al2(CO3)3 + ___ H2O";
        // 4
        if (!balance("__ CH3CH2CH2CH3 + ___ O2", "___ CO2 + ___ H2O ").getBalancedEquation().equals("2(CH3CH2CH2CH3) + 13O2 = 8(CO2) + 10(H2O)"))
            return "__ CH3CH2CH2CH3 + ___ O2 = ___ CO2 + ___ H2O ";
        // 5
        if (!balance("_ NH4OH + ___ H3PO4", "___ (NH4)3PO4 + ___ H2O").getBalancedEquation().equals("3(NH4OH) + H3PO4 = (NH4)3PO4 + 3(H2O)"))
            return "_ NH4OH + ___ H3PO4 = ___ (NH4)3PO4 + ___ H2O";
        // 6
        if (!balance(" H3PO4 + ___ Ca(OH)2", "___ Ca3(PO4)2 + ___ H2O").getBalancedEquation().equals("2(H3PO4) + 3(Ca(OH)2) = Ca3(PO4)2 + 6(H2O)"))
            return " H3PO4 + ___ Ca(OH)2 = ___ Ca3(PO4)2 + ___ H2O";
        // 7
        if (!balance("Ca3(PO4)2 + ___ SiO2 + ___ C", "___ CaSiO3 + ___ CO + ___ P ").getBalancedEquation().equals("Ca3(PO4)2 + 3(SiO2) + 5C = 3(CaSiO3) + 5(CO) + 2P"))
            return "Ca3(PO4)2 + ___ SiO2 + ___ C = ___ CaSiO3 + ___ CO + ___ P ";
        // 8
        if (!balance("2NH3+H2SO4", "(NH4)2SO4").getBalancedEquation().equals("2(NH3) + H2SO4 = (NH4)2SO4"))
            return "2NH3+H2SO4=(NH4)2SO4";
        // 9
        if (!balance("2KClO3", "KCl+O2").getBalancedEquation().equals("2(KClO3) = 2(KCl) + 3O2"))
            return "2KClO3=KCl+O2";
        // 10
        if (!balance("2H2+O2", "H2O").getBalancedEquation().equals("2H2 + O2 = 2(H2O)"))
            return "2H2+O2=H2O";
        // 12
        if (!balance("2C6H14+O2", "CO2+H2O").getBalancedEquation().equals("2(C6H14) + 19O2 = 12(CO2) + 14(H2O)"))
            return "2C6H14+O2=CO2+H2O";
        // 12
        if (!balance("Fe+2HCl", "FeCl2+H").getBalancedEquation().equals("Fe + 2(HCl) = FeCl2 + 2H"))
            return "Fe+2HCl=FeCl2+H";
        // 13
        if (!balance("C2H5OH+3O2", "2CO2+3H2O").getBalancedEquation().equals("C2H5OH + 3O2 = 2(CO2) + 3(H2O)"))
            return "C2H5OH+3O2=2CO2+3H2O";
        // 14
        if (!balance("C3H8+5O2", "CO2+H2O").getBalancedEquation().equals("C3H8 + 5O2 = 3(CO2) + 4(H2O)"))
            return "C3H8+5O2=CO2+H2O";
        // 15
        if (!balance("2Ca3(PO4)2 + ___ 2SiO2 + ___ 2C", "___ 2CaSiO3 + ___ 2CO + ___ 2P").getBalancedEquation().equals("2(Ca3(PO4)2) + 6(SiO2) + 10C = 6(CaSiO3) + 10(CO) + 4P"))
            return "2Ca3(PO4)2 + ___ 2SiO2 + ___ 2C = ___ 2CaSiO3 + ___ 2CO + ___ 2P";
        // 17
        if (!balance("C6H12O6+O2", "CO2+H2O").getBalancedEquation().equals("C6H12O6 + 6O2 = 6(CO2) + 6(H2O)"))
            return "C6H12O6+O2=CO2+H2O";
        // 18
        if (!balance("Fe2O3+C", "Fe+CO2").getBalancedEquation().equals("2(Fe2O3) + 3C = 4Fe + 3(CO2)"))
            return "Fe2O3+C=Fe+CO2";
        // 19
        if (!balance("NH4NO3", "N2+O2+H2O").getBalancedEquation().equals("2(NH4NO3) = 2N2 + O2 + 4(H2O)"))
            return "NH4NO3=N2+O2+H2O";
        // 20
        if (!balance("P4+O2", "P4O10").getBalancedEquation().equals("P4 + 5O2 = P4O10"))
            return "P4+O2=P4O10";
        //21
        if (!balance("Cl + 2OP", "Cl2O + P").getBalancedEquation().equals("4Cl + 2(OP) = 2(Cl2O) + 2P"))
            return "Cl + 2(OP)= Cl2O + P";

        if (!balance("++++Cl +++ 2OP++++", "+++Cl2O + P+").getBalancedEquation().equals("4Cl + 2(OP) = 2(Cl2O) + 2P"))
            return "++++Cl +++ 2OP++++= +++Cl2O + P+";

        if (!balance("((P4+O2", "P()4O))10)").getBalancedEquation().equals("P4 + 5O2 = P4O10"))
            return "P4+O2=P4O10";

        if (!balance("H2S + O4", "H + S + O").getBalancedEquation().equals("H2S + O4 = 2H + S + 4O"))
            return "H2S + O4 = H + S + O";

        return "OK";
    }

}
