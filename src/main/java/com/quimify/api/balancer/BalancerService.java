package com.quimify.api.balancer;

import com.quimify.api.error.ErrorService;

import java.util.*;
import java.util.List;

import com.quimify.api.metrics.MetricsService;
import com.quimify.api.notfoundquery.NotFoundQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
class BalancerService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    NotFoundQueryService notFoundQueryService;

    @Autowired
    ErrorService errorService;

    @Autowired
    MetricsService metricsService;

    BalancerResult tryBalance(String equation) {
        BalancerResult balancerResult;

        try {
            balancerResult = balance(equation);

            if (!balancerResult.isPresent())
                logger.warn("Couldn't calculate \"" + equation + "\". " + "RESULT: " + balancerResult.getError());
        } catch (StackOverflowError error) {
            errorService.log("StackOverflow error", equation, getClass());
            balancerResult = BalancerResult.error("Error: La reacción es demasiado larga.");
        } catch (Exception exception) {
            errorService.log("Exception calculating: " + equation, exception.toString(), getClass());
            balancerResult = BalancerResult.error("Error: Excepción en el cálculo");
        }

        if (!balancerResult.isPresent())
            notFoundQueryService.log(equation, getClass());

        metricsService.balanceEquationQueried(balancerResult.isPresent());

        return balancerResult;
    }

    /**
     * Driver Method of Solving Algorithm. Ties up use of all classes. First, matrix is solved using gaussian elimination algorithm.
     * Then, reduced matrix is made into algebraic equations and solved for variables in terms of last variable(n-1 x n matrix).
     * Last, the lcm of all the denominators are multiplied to give the simplified solution.
     */
    BalancerResult balance(String equation) {
        Hashtable<Integer, Hashtable<String, Integer>> reactants;
        Hashtable<Integer, Hashtable<String, Integer>> products;

        Matrix matrix;
        Hashtable<Integer, LinkedList<Integer>> finalSolution;

        equation = equation.replaceAll("=+", "=");
        String originalReactantsString;
        String originalProductsString;
        if (equation.contains("=")) {
            String[] arr = equation.split("=");
            originalReactantsString = removeUnnecessaryCharacters(arr[0].replace(" ", ""));
            originalProductsString = removeUnnecessaryCharacters(arr[1].replace(" ", ""));
        } else {
            return BalancerResult.error("Error: Falta una parte de la reacción");
        }

        String normalizedReactantString = normalizeEquation(originalReactantsString);
        String normalizedProductString = normalizeEquation(originalProductsString);

        //Process Input
        List<Object> parseReactants = parseString(normalizedReactantString);
        reactants = (Hashtable<Integer, Hashtable<String, Integer>>) parseReactants.get(0);

        List<Object> parseProducts = parseString(normalizedProductString);
        products = (Hashtable<Integer, Hashtable<String, Integer>>) parseProducts.get(0);

        LinkedHashSet<String> reactantsElements = getElements(originalReactantsString); //elementService.get(originalProductsString)
        LinkedHashSet<String> productsElements = getElements(originalProductsString); //elementService.get(originalReactantsString)

        if (reactantsElements.equals(productsElements)) {
            //Making Matrix
            int[][] intMatrix = new int[reactantsElements.size()][reactants.size() + products.size()];
            int currentIndex = 0;

            for (String element : reactantsElements) {
                int currentRowIndex = 0;
                int[] intMatrixRow = new int[reactants.size() + products.size()];

                for (int i = 0; i < reactants.size(); i++) {
                    Hashtable<String, Integer> results = reactants.get(i);
                    intMatrixRow[currentRowIndex] = (results.getOrDefault(element, 0));
                    currentRowIndex += 1;
                }

                for (int i = 0; i < products.size(); i++) {
                    Hashtable<String, Integer> results = products.get(i);
                    intMatrixRow[currentRowIndex] = ((products.size() - 1 == i ? 1 : -1) * results.getOrDefault(element, 0));
                    currentRowIndex += 1;
                }

                intMatrix[currentIndex] = intMatrixRow;
                currentIndex += 1;
            }

            matrix = new Matrix(intMatrix);
            finalSolution = new Hashtable<>();

        } else {
            return BalancerResult.error("Error: Deben aparecer los mismos elementos en ambas partes de la reacción");
        }

        matrix.gaussJordanElimination();

        Fraction[] solutions = new Fraction[matrix.columns()];

        int j = 0;
        for (int i = 0; i < matrix.rows(); i++) {
            if (!matrix.get(i, matrix.columns() - 1).equals(Fraction.zero())) {
                solutions[j] = matrix.get(i, matrix.columns() - 1);
                j++;
            }
        }

        solutions[matrix.columns() - 1] = Fraction.one();

        // Check if the equation is balanceable.
        if (!isBalanceable(solutions)) {
            return BalancerResult.error("Error: La reacción no es balanceable");
        }

        int lcm = 1;

        for (Fraction f : solutions) {
            lcm = Fraction.leastCommonMultiple(lcm, f.getDenominator());
        }
        for (int i = 0; i < solutions.length; i++) {
            solutions[i] = solutions[i].times(new Fraction(lcm));
        }

        finalSolution.put(0, implementSubstitution(Arrays.copyOfRange(solutions, 0, reactants.size())));
        finalSolution.put(1, implementSubstitution(Arrays.copyOfRange(solutions, reactants.size(), solutions.length)));

        return new BalancerResult(true, equation, originalReactantsString, originalProductsString,
                formatSolution(originalReactantsString, finalSolution.get(0)) + " = " + formatSolution(originalProductsString, finalSolution.get(1)),
                        formatSolution(originalReactantsString, finalSolution.get(0)), formatSolution(originalProductsString, finalSolution.get(1)));
    }

    private boolean isBalanceable(Fraction[] solutions) {
        if (solutions == null || solutions.length == 0)
            return false;

        return Arrays.stream(solutions).noneMatch(Objects::isNull);
    }

    /**
     * This function is used to solve the bug of initial prefix coefficients always turning to 1 when outputting the solution
     * it gets the position and the value of the coefficient to later switch it in the output.
     */

    private static String removeCoefficients(String equation) {
        StringBuilder removedCoefficients = new StringBuilder();
        int contador = 0;
        boolean isCoefficient = true;

        while (contador < equation.length()) {
            char character = equation.charAt(contador);

            if (Character.isDigit(character) && isCoefficient) {
                while (Character.isDigit(character)) {
                    character = equation.charAt(contador + 1);
                    contador++;
                }
            }

            else {
                // Handle different scenarios based on the character encountered
                if (character == '+') {// Reset for the next part of the equation
                    isCoefficient = true;
                    removedCoefficients.append(character);
                }
                else {
                    isCoefficient = false;
                    removedCoefficients.append(character);
                }
                contador++;
            }
        }

        return removedCoefficients.toString();
    }

    private static String normalizeEquation(String equation) {
        StringBuilder normalizedEquation = new StringBuilder();
        equation= equation.concat("++");
        int coefficient = 1;
        String multiDigitSuffix = "";
        boolean isCoefficient = true;
        int contador = 0;

        while (contador < equation.length() - 2) {
            char character = equation.charAt(contador);

            if (Character.isDigit(character) && isCoefficient) {
                String multiDigitCoefficient = "";
                while (Character.isDigit(character)){
                    multiDigitCoefficient = multiDigitCoefficient.concat(String.valueOf(character));
                    character = equation.charAt(contador + 1);
                    contador++;
                }
                // Accumulate coefficient value if it's multi-digit
                coefficient = Integer.parseInt(multiDigitCoefficient);
            }
            else {
                // Handle different scenarios based on the character encountered
                switch (character) {
                    case '+':
                        // Reset for the next part of the equation
                        isCoefficient = true;
                        coefficient = 1;
                        normalizedEquation.append(character);
                        contador++;
                        break;
                    case '(':
                        normalizedEquation.append(character);
                        character = equation.charAt(contador + 1);
                        while (character != ')'){
                            normalizedEquation.append(character);
                            contador++;
                            character = equation.charAt(contador + 1);
                        }
                        contador++;
                        break;
                    case ')':
                        isCoefficient = false;
                        normalizedEquation.append(character);

                        while (Character.isDigit(equation.charAt(contador + 1))){
                            character = equation.charAt(contador + 1);

                            //Por si el sufijo tiene más de un caracter
                            multiDigitSuffix = multiDigitSuffix.concat(String.valueOf(character));
                            contador++;

                            if (contador + 1 == equation.length()) {
                                break;
                            }
                        }
                        if (multiDigitSuffix.equals(""))
                            multiDigitSuffix = "1";

                        normalizedEquation.append(coefficient * Integer.parseInt(multiDigitSuffix));
                        contador++;
                        multiDigitSuffix = "";
                        break;
                    default:
                        // Apply coefficient to each element or compound
                        isCoefficient = false;
                        if (Character.isDigit(character)) {
                            multiDigitSuffix = multiDigitSuffix.concat(String.valueOf(character));
                            if (contador + 1 != equation.length()) {
                                while (Character.isDigit(equation.charAt(contador + 1))){
                                    character = equation.charAt(contador + 1);

                                    //Por si el sufijo tiene más de un caracter
                                    multiDigitSuffix = multiDigitSuffix.concat(String.valueOf(character));
                                    contador++;

                                    if (contador + 1 == equation.length()) {
                                        break;
                                    }
                                }
                            }

                            normalizedEquation.append(coefficient * Integer.parseInt(multiDigitSuffix));
                            multiDigitSuffix = "";
                        }
                        else {
                            normalizedEquation.append(character);
                            // Si un caracter es mayuscula y el siguiente también, en medio hay un coeficiente = 1
                            // si un caracter es mayuscula, caracter + 1 minuscula y caracter + 2 mayusucla => coeficiente = 1
                            if (Character.isUpperCase(character)
                                    && (Character.isUpperCase(equation.charAt(contador + 1)) || equation.charAt(contador + 1) == '+'
                                    || equation.charAt(contador + 1) == '(' || equation.charAt(contador + 1) == ')')
                                    /*&& (contador + 1 <= equation.length()) || contador + 1 >= equation.length()*/){
                                if (coefficient != 1){
                                    normalizedEquation.append(coefficient);
                                }
                            }
                            else if (Character.isUpperCase(character)
                                    && Character.isLowerCase(equation.charAt(contador + 1))
                                    && (Character.isUpperCase(equation.charAt(contador + 2)) || equation.charAt(contador + 2) == '+'
                                    || equation.charAt(contador + 2) == '(' || equation.charAt(contador + 2) == ')')
                                    /*&& (contador + 1 <= equation.length()) || contador + 1 >= equation.length()
                                    && (contador + 2 <= equation.length()) || contador + 2 >= equation.length()*/){
                                contador++;
                                character = equation.charAt(contador);
                                normalizedEquation.append(character);
                                if (coefficient != 1){
                                    normalizedEquation.append(coefficient);
                                }
                            }
                        }
                        contador++;
                        break;
                }
            }
        }

        return normalizedEquation.toString();
    }

    /**
     * Converts FractionComponents into integers for final formatting for either reactant or product side.
     */
    private static LinkedList<Integer> implementSubstitution(Fraction[] arr) {
        LinkedList<Integer> finalCoefficients = new LinkedList<>();
        for (Fraction f : arr) {
            finalCoefficients.addLast(f.getNumerator());
        }
        return finalCoefficients;
    }

    /**
     * Gets all elements used on a side an equation
     */
    private static LinkedHashSet<String> getElements(String inputString) {
        LinkedHashSet<String> elements = new LinkedHashSet<>();
        String elementString = "";
        char character = 0;
        for (int i = 0; i < inputString.length(); i++) {
            character = inputString.charAt(i);
            if (Character.isLetter(character)) {
                if (String.valueOf(character).toUpperCase().equals(String.valueOf(character))) {
                    if (!elementString.equals("")) {
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
        if (!Character.toString(character).equals("")) {
            elements.add(elementString);
        }
        return elements;
    }

    /**
     * Parses string to get Hashtable representation of a side of a chemical equation.
     */
    private List<Object> parseString(String inputString) {
        LinkedList<String> compoundStringTable = new LinkedList<>();
        Hashtable<Integer, Hashtable<String, Integer>> compoundTable = new Hashtable<>();
        StringBuilder storeString = new StringBuilder();
        Integer index = 0;
        int coefficient = 1; // Default coefficient
        StringBuilder coefficientBuilder = new StringBuilder(); // To handle multi-digit coefficients

        for (int j = 0; j < inputString.length(); j++) {
            char c = inputString.charAt(j);
            if (Character.toString(c).equals("+")) {
                if (storeString.length() > 0) {
                    compoundStringTable.add(storeString.toString());
                    // Parse the coefficient
                    if (coefficientBuilder.length() > 0) {
                        coefficient = Integer.parseInt(coefficientBuilder.toString());
                        coefficientBuilder = new StringBuilder(); // Reset for next coefficient
                    }
                    compoundTable.put(index, parseCompound(storeString.toString(), coefficient));
                    storeString = new StringBuilder();
                    index++;
                    coefficient = 1; // Reset coefficient after processing a compound
                }
            } else if (Character.isDigit(c) && storeString.length() == 0) {
                // If the character is a digit and storeString is empty, we're reading a coefficient
                coefficientBuilder.append(c);
            } else {
                storeString.append(c);
            }
        }
        if (storeString.length() > 0) {
            if (coefficientBuilder.length() > 0) {
                coefficient = Integer.parseInt(coefficientBuilder.toString());
            }
            compoundStringTable.add(storeString.toString());
            compoundTable.put(index, parseCompound(storeString.toString(), coefficient));
        }
        return Arrays.asList(compoundTable, compoundStringTable);
    }



    /**
     * Removes all characters that are not letters, numbers, parentheses(), and plus signs("+")
     */
    private static String removeUnnecessaryCharacters(String currentString) {
        // Replace multiple '+' signs with a single '+'
        currentString = currentString.replaceAll("\\++", "+");
        // Remove '+' at the beginning and end of the string, if present
        currentString = currentString.replaceAll("^\\++|\\++$", "");
        // Remove any other non-essential characters except parentheses for balance check
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

    /**
     * String formatting of solution
     */
    private static String formatSolution(String originalString, LinkedList<Integer> solutions) {
        String[] arr = originalString.split("\\+");
        StringBuilder s = new StringBuilder();
        String coefficientHandler = "";

        for (int i = 0; i < solutions.size(); i++) {
            if (Character.isDigit(arr[i].charAt(0))){
                int j = 0;
                while (Character.isDigit(arr[i].charAt(j))){
                    coefficientHandler = coefficientHandler.concat(String.valueOf(arr[i].charAt(j)));
                    j++;
                }
                arr[i] = arr[i].substring(j);
                int newCoefficient = Integer.parseInt(coefficientHandler) * solutions.get(i);

                if (newCoefficient != 1) { // Only append if coefficient is not 1
                    s.append(newCoefficient);
                }

                appendElement(arr, i, s, solutions, newCoefficient);

                coefficientHandler = "";
            }
            else{
                if (solutions.get(i) != 1) { // Only append if coefficient is not 1
                    s.append(solutions.get(i));
                }

                appendElement(arr, i, s, solutions, solutions.get(i));

            }
        }
        return s.toString();
    }

    private static void appendElement(String[] arr, int i, StringBuilder s, LinkedList<Integer> solutions, int newCoefficient) {

        if (arr[i].length() == 2 && (Character.isDigit(arr[i].charAt(1)) || Character.isLowerCase(arr[i].charAt(1))))
            s.append(arr[i]);
        else if (arr[i].length() == 3 && Character.isDigit(arr[i].charAt(2)) && Character.isLowerCase(arr[i].charAt(1)))
            s.append(arr[i]);
        else{
            if (arr[i].length() > 1 && newCoefficient != 1)
                s.append('(');

            s.append(arr[i]);

            if (arr[i].length() > 1 && newCoefficient != 1)
                s.append(')');
        }

        if (i < solutions.size() - 1)
            s.append(" + ");
    }


    /**
     * Parses each compound to get Hashtable of elements and quantities in compound
     */
    private static Hashtable<String, Integer> parseCompound(String inputString, Integer coefficient) {
        Hashtable<String, Integer> dictionary = new Hashtable<>();
        String symbol = "";
        String numString = "";
        StringBuilder paranthesesStoreString = new StringBuilder();
        boolean parenthesesOn = false;
        boolean parenthesesEnd = false;
        String parenthesesScaler = "";
        for (int i = 0; i < inputString.length(); i++) {
            char character = inputString.charAt(i);
            if (Character.isLetter(character)) {
                //Checks that this is a letter
                if (String.valueOf(character).toUpperCase().equals(String.valueOf(character))) {
                    if (!parenthesesOn && !parenthesesEnd) {
                        //This is uppercase
                        if (!symbol.equals("")) {
                            //Symbol is filled and needs to be dumped
                            int quantity = numString.equals("") ? 1 : Integer.parseInt(numString);
                            // Apply the coefficient here
                            quantity *= coefficient;

                            if (dictionary.containsKey(symbol)) {
                                // If the symbol is already in the dictionary, add the quantity
                                dictionary.put(symbol, dictionary.get(symbol) + quantity);
                            } else {
                                // Otherwise, just put the new quantity in the dictionary
                                dictionary.put(symbol, quantity);
                            }
                            symbol = "";
                            numString = "";
                        }
                        symbol = symbol.concat(String.valueOf(character));
                    } else if (parenthesesOn && !parenthesesEnd) {
                        paranthesesStoreString.append(character);
                    } else {
                        Hashtable<String, Integer> parenthesesParse = parseCompound(paranthesesStoreString.toString(), coefficient);
                        if (parenthesesScaler.equals(""))
                            parenthesesScaler = "1";
                        for (String key : parenthesesParse.keySet()) {
                            if (!dictionary.containsKey(key)) {
                                dictionary.put(key, parenthesesParse.get(key) * Integer.parseInt(parenthesesScaler));
                            } else {
                                dictionary.put(key, parenthesesParse.get(key) * Integer.parseInt(parenthesesScaler) + dictionary.get(key));
                            }
                        }
                        paranthesesStoreString = new StringBuilder();
                        parenthesesEnd = false;
                        parenthesesScaler = "";
                        symbol = symbol.concat(String.valueOf(character));
                    }
                } else {
                    if (!parenthesesOn)
                        symbol = symbol.concat(String.valueOf(character));
                    else
                        paranthesesStoreString.append(character);
                }
            } else if (Character.isDigit(character)) {
                //This is a number
                if (!parenthesesOn && !parenthesesEnd) {
                    numString = numString.concat(String.valueOf(character));
                } else if (parenthesesEnd && !parenthesesOn) {
                    parenthesesScaler += character;
                } else if (!parenthesesEnd) {
                    paranthesesStoreString.append(character);
                }
            } else if (character == '(') {
                //Start Statement
                if (!parenthesesEnd) {
                    parenthesesOn = true;
                    if (!dictionary.containsKey(symbol)) {
                        try {
                            dictionary.put(symbol, Integer.valueOf(numString));
                        } catch (NumberFormatException exception) {
                            dictionary.put(symbol, 1);
                        }
                    } else {
                        try {
                            dictionary.put(symbol, Integer.parseInt(numString) + dictionary.get(symbol));
                        } catch (NumberFormatException exception) {
                            dictionary.put(symbol, 1 + dictionary.get(symbol));
                        }
                    }
                    symbol = "";
                    numString = "";
                } else {
                    Hashtable<String, Integer> parenthesesParse = parseCompound(paranthesesStoreString.toString(), coefficient);
                    if (parenthesesScaler.equals(""))
                        parenthesesScaler = "1";
                    for (String key : parenthesesParse.keySet()) {
                        if (!dictionary.containsKey(key)) {
                            dictionary.put(key, parenthesesParse.get(key) * Integer.parseInt(parenthesesScaler));
                        } else {
                            dictionary.put(key, parenthesesParse.get(key) * Integer.parseInt(parenthesesScaler) + dictionary.get(key));
                        }
                    }
                    paranthesesStoreString = new StringBuilder();
                    parenthesesOn = true;
                    parenthesesEnd = false;
                    parenthesesScaler = "";
                }
            } else if (character == ')') {
                //End statement
                parenthesesEnd = true;
                parenthesesOn = false;
            }
        }
        if (!parenthesesEnd) {
            if (numString.equals("")) {
                numString = "1";
            }
            if (!dictionary.containsKey(symbol)) {
                dictionary.put(symbol, Integer.valueOf(numString));
            } else {
                dictionary.put(symbol, Integer.parseInt(numString) + dictionary.get(symbol));
            }
        } else {
            Hashtable<String, Integer> parenthesesParse = parseCompound(paranthesesStoreString.toString(), coefficient);
            if (parenthesesScaler.equals(""))
                parenthesesScaler = "1";
            for (String key : parenthesesParse.keySet()) {
                if (!dictionary.containsKey(key)) {
                    dictionary.put(key, parenthesesParse.get(key) * Integer.parseInt(parenthesesScaler));
                } else {
                    dictionary.put(key, parenthesesParse.get(key) * Integer.parseInt(parenthesesScaler) + dictionary.get(key));
                }
            }
        }
        return dictionary;
    }

    String testTemporal() {
        // 1
        if (!balance("2H + O = H3O").getBalancedEquation().equals("6H + 2O = 2(H3O)"))
            return "2H + O = H3O";
        // 2
        if (!balance("H3PO4 + ___ Mg(OH)2 = ___ Mg3(PO4)2 + ___ H2O").getBalancedEquation().equals("2(H3PO4) + 3(Mg(OH)2) = Mg3(PO4)2 + 6(H2O)"))
            return "H3PO4 + ___ Mg(OH)2 = ___ Mg3(PO4)2 + ___ H2O";
        // 3
        if (!balance("Al (OH)3 + ___ H2CO3 = ___ Al2(CO3)3 + ___ H2O").getBalancedEquation().equals("2(Al(OH)3) + 3(H2CO3) = Al2(CO3)3 + 6(H2O)"))
            return "Al (OH)3 + ___ H2CO3 = ___ Al2(CO3)3 + ___ H2O";
        // 4
        if (!balance("__ CH3CH2CH2CH3 + ___ O2 = ___ CO2 + ___ H2O ").getBalancedEquation().equals("2(CH3CH2CH2CH3) + 13O2 = 8(CO2) + 10(H2O)"))
            return "__ CH3CH2CH2CH3 + ___ O2 = ___ CO2 + ___ H2O ";
        // 5
        if (!balance("_ NH4OH + ___ H3PO4 = ___ (NH4)3PO4 + ___ H2O").getBalancedEquation().equals("3(NH4OH) + H3PO4 = (NH4)3PO4 + 3(H2O)"))
            return "_ NH4OH + ___ H3PO4 = ___ (NH4)3PO4 + ___ H2O";
        // 6
        if (!balance(" H3PO4 + ___ Ca(OH)2 = ___ Ca3(PO4)2 + ___ H2O").getBalancedEquation().equals("2(H3PO4) + 3(Ca(OH)2) = Ca3(PO4)2 + 6(H2O)"))
            return " H3PO4 + ___ Ca(OH)2 = ___ Ca3(PO4)2 + ___ H2O";
        // 7
        if (!balance("Ca3(PO4)2 + ___ SiO2 + ___ C = ___ CaSiO3 + ___ CO + ___ P ").getBalancedEquation().equals("Ca3(PO4)2 + 3(SiO2) + 5C = 3(CaSiO3) + 5(CO) + 2P"))
            return "Ca3(PO4)2 + ___ SiO2 + ___ C = ___ CaSiO3 + ___ CO + ___ P ";
        // 8
        if (!balance("2NH3+H2SO4=(NH4)2SO4").getBalancedEquation().equals("2(NH3) + H2SO4 = (NH4)2SO4"))
            return "2NH3+H2SO4=(NH4)2SO4";
        // 9
        if (!balance("2KClO3=KCl+O2").getBalancedEquation().equals("2(KClO3) = 2(KCl) + 3O2"))
            return "2KClO3=KCl+O2";
        // 10
        if (!balance("2H2+O2=H2O").getBalancedEquation().equals("2H2 + O2 = 2(H2O)"))
            return "2H2+O2=H2O";
        // 12
        if (!balance("2C6H14+O2=CO2+H2O").getBalancedEquation().equals("2(C6H14) + 19O2 = 12(CO2) + 14(H2O)"))
            return "2C6H14+O2=CO2+H2O";
        // 12
        if (!balance("Fe+2HCl=FeCl2+H").getBalancedEquation().equals("Fe + 2(HCl) = FeCl2 + 2H"))
            return "Fe+2HCl=FeCl2+H";
        // 13
        if (!balance("C2H5OH+3O2=2CO2+3H2O").getBalancedEquation().equals("C2H5OH + 3O2 = 2(CO2) + 3(H2O)"))
            return "C2H5OH+3O2=2CO2+3H2O";
        // 14
        if (!balance("C3H8+5O2=CO2+H2O").getBalancedEquation().equals("C3H8 + 5O2 = 3(CO2) + 4(H2O)"))
            return "C3H8+5O2=CO2+H2O";
        // 15
        if (!balance("2Ca3(PO4)2 + ___ 2SiO2 + ___ 2C = ___ 2CaSiO3 + ___ 2CO + ___ 2P").getBalancedEquation().equals("2(Ca3(PO4)2) + 6(SiO2) + 10C = 6(CaSiO3) + 10(CO) + 4P"))
            return "2Ca3(PO4)2 + ___ 2SiO2 + ___ 2C = ___ 2CaSiO3 + ___ 2CO + ___ 2P";
        // 16
        if (balance("H2+O2 = H2O+O3").getBalancedEquation() != null)
            return "H2+O2 = H2O+O3";
        // 17
        if (!balance("C6H12O6+O2=CO2+H2O").getBalancedEquation().equals("C6H12O6 + 6O2 = 6(CO2) + 6(H2O)"))
            return "C6H12O6+O2=CO2+H2O";
        // 18
        if (!balance("Fe2O3+C=Fe+CO2").getBalancedEquation().equals("2(Fe2O3) + 3C = 4Fe + 3(CO2)"))
            return "Fe2O3+C=Fe+CO2";
        // 19
        if (!balance("NH4NO3=N2+O2+H2O").getBalancedEquation().equals("2(NH4NO3) = 2N2 + O2 + 4(H2O)"))
            return "NH4NO3=N2+O2+H2O";
        // 20
        if (!balance("P4+O2=P4O10").getBalancedEquation().equals("P4 + 5O2 = P4O10"))
            return "P4+O2=P4O10";
        //21
        if (!balance("Cl + 2OP= Cl2O + P").getBalancedEquation().equals("4Cl + 2(OP) = 2(Cl2O) + 2P"))
            return "Cl + 2(OP)= Cl2O + P";

        if (!balance("++++Cl +++ 2OP++++= +++Cl2O + P+").getBalancedEquation().equals("4Cl + 2(OP) = 2(Cl2O) + 2P"))
            return "++++Cl +++ 2OP++++= +++Cl2O + P+";

        if (!balance("((P4+O2=P()4O))10)").getBalancedEquation().equals("P4 + 5O2 = P4O10"))
            return "P4+O2=P4O10";

        return "OK";
    }

}

