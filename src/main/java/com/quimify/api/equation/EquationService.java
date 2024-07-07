package com.quimify.api.equation;

import com.quimify.api.error.ErrorService;

import java.util.*;

import com.quimify.api.metrics.MetricsService;
import com.quimify.api.notfoundquery.NotFoundQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class EquationService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    NotFoundQueryService notFoundQueryService;

    @Autowired
    ErrorService errorService;

    @Autowired
    MetricsService metricsService;

    // **********************************************************************************************************
    // **********************************************************************************************************
    // **********************************************************************************************************
    // TODO rewrite this awful code from the internet
    // **********************************************************************************************************
    // **********************************************************************************************************
    // **********************************************************************************************************

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

    // TODO split in different methods
    private EquationResult balance(String reactantsText, String productsText) {
        reactantsText = removeUnnecessaryCharacters(reactantsText);
        productsText = removeUnnecessaryCharacters(productsText);

        LinkedHashSet<String> reactantsElements = getElements(reactantsText); // TODO type?
        LinkedHashSet<String> productsElements = getElements(productsText);

        if (!reactantsElements.equals(productsElements))
            return EquationResult.error("Deben aparecer los mismos elementos en ambas partes de la reacción.");

        String normalizedReactantString = normalizeEquation(reactantsText);
        String normalizedProductString = normalizeEquation(productsText);

        Hashtable<Integer, Hashtable<String, Integer>> reactants = parseString(normalizedReactantString); // TODO use Formula class?
        Hashtable<Integer, Hashtable<String, Integer>> products = parseString(normalizedProductString);

        Matrix matrix = equationMatrix(reactants, products, reactantsElements);
        Fraction[] solutions = Mathematics.solve(matrix);

        if (!isBalanceable(solutions))
            return EquationResult.error("La reacción no es balanceable.");

        Fraction[] solutionsAndOne = new Fraction[solutions.length + 1]; // TODO why?
        System.arraycopy(solutions, 0, solutionsAndOne, 0, solutions.length);
        solutionsAndOne[solutionsAndOne.length - 1] = Fraction.ONE;

        // "Last, the lcm of all the denominators are multiplied to give the simplified solution."

        int lcm = 1;

        for (Fraction f : solutionsAndOne) {
            lcm = Mathematics.leastCommonMultiple(lcm, f.getDenominator());
        }
        for (int i = 0; i < solutionsAndOne.length; i++) {
            solutionsAndOne[i] = solutionsAndOne[i].times(new Fraction(lcm));
        }

        String resultReactants = formatSolution(reactantsText, implementSubstitution(Arrays.copyOfRange(solutionsAndOne, 0, reactants.size())));
        String resultProducts = formatSolution(productsText, implementSubstitution(Arrays.copyOfRange(solutionsAndOne, reactants.size(), solutionsAndOne.length)));

        return new EquationResult(resultReactants, resultProducts);
    }

    // TODO fix all these methods:

    private Matrix equationMatrix(Hashtable<Integer, Hashtable<String, Integer>> reactants, Hashtable<Integer, Hashtable<String, Integer>> products, LinkedHashSet<String> elements) {
        Matrix matrix = new Matrix(elements.size(), reactants.size() + products.size());

        int currentIndex = 0;

        for (String element : elements) {
            int currentRowIndex = 0;

            for (int i = 0; i < reactants.size(); i++) {
                Hashtable<String, Integer> results = reactants.get(i);
                matrix.set(currentIndex, currentRowIndex, new Fraction(results.getOrDefault(element, 0)));
                currentRowIndex += 1;
            }

            for (int i = 0; i < products.size(); i++) {
                Hashtable<String, Integer> results = products.get(i);
                matrix.set(currentIndex, currentRowIndex, new Fraction(((products.size() - 1 == i ? 1 : -1) * results.getOrDefault(element, 0))));
                currentRowIndex += 1;
            }

            currentIndex += 1;
        }

        return matrix;
    }

    private boolean isBalanceable(Fraction[] solutions) {
        return Arrays.stream(solutions).noneMatch(Objects::isNull);
    }

    private static String normalizeEquation(String equation) {
        StringBuilder normalizedEquation = new StringBuilder();
        equation = equation.concat("++");
        int coefficient = 1;
        String multiDigitSuffix = "";
        boolean isCoefficient = true;
        int contador = 0;

        while (contador < equation.length() - 2) {
            char character = equation.charAt(contador);

            if (Character.isDigit(character) && isCoefficient) {
                String multiDigitCoefficient = "";
                while (Character.isDigit(character)) {
                    multiDigitCoefficient = multiDigitCoefficient.concat(String.valueOf(character));
                    character = equation.charAt(contador + 1);
                    contador++;
                }
                // Accumulate coefficient value if it's multi-digit
                coefficient = Integer.parseInt(multiDigitCoefficient);
            } else {
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
                        while (character != ')') {
                            normalizedEquation.append(character);
                            contador++;
                            character = equation.charAt(contador + 1);
                        }
                        contador++;
                        break;
                    case ')':
                        isCoefficient = false;
                        normalizedEquation.append(character);

                        while (Character.isDigit(equation.charAt(contador + 1))) {
                            character = equation.charAt(contador + 1);

                            //Por si el sufijo tiene más de un caracter
                            multiDigitSuffix = multiDigitSuffix.concat(String.valueOf(character));
                            contador++;

                            if (contador + 1 == equation.length()) {
                                break;
                            }
                        }
                        if (multiDigitSuffix.isEmpty())
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
                                while (Character.isDigit(equation.charAt(contador + 1))) {
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
                        } else {
                            normalizedEquation.append(character);
                            // Si un caracter es mayuscula y el siguiente también, en medio hay un coeficiente = 1
                            // si un caracter es mayuscula, caracter + 1 minuscula y caracter + 2 mayusucla => coeficiente = 1
                            if (Character.isUpperCase(character)
                                    && (Character.isUpperCase(equation.charAt(contador + 1)) || equation.charAt(contador + 1) == '+'
                                    || equation.charAt(contador + 1) == '(' || equation.charAt(contador + 1) == ')')
                                /*&& (contador + 1 <= equation.length()) || contador + 1 >= equation.length()*/) {
                                if (coefficient != 1) {
                                    normalizedEquation.append(coefficient);
                                }
                            } else if (Character.isUpperCase(character)
                                    && Character.isLowerCase(equation.charAt(contador + 1))
                                    && (Character.isUpperCase(equation.charAt(contador + 2)) || equation.charAt(contador + 2) == '+'
                                    || equation.charAt(contador + 2) == '(' || equation.charAt(contador + 2) == ')')
                                    /*&& (contador + 1 <= equation.length()) || contador + 1 >= equation.length()
                                    && (contador + 2 <= equation.length()) || contador + 2 >= equation.length()*/) {
                                contador++;
                                character = equation.charAt(contador);
                                normalizedEquation.append(character);
                                if (coefficient != 1) {
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
    // TODO type?
    private static LinkedList<Integer> implementSubstitution(Fraction[] arr) {
        LinkedList<Integer> finalCoefficients = new LinkedList<>();

        for (Fraction f : arr) {
            // TODO check denominator is 1
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

    /**
     * Parses string to get Hashtable representation of a side of a chemical equation.
     */
    private Hashtable<Integer, Hashtable<String, Integer>> parseString(String inputString) {
        Hashtable<Integer, Hashtable<String, Integer>> compoundTable = new Hashtable<>();
        StringBuilder storeString = new StringBuilder();
        Integer index = 0;
        int coefficient = 1; // Default coefficient
        StringBuilder coefficientBuilder = new StringBuilder(); // To handle multi-digit coefficients

        for (int j = 0; j < inputString.length(); j++) {
            char c = inputString.charAt(j);
            if (Character.toString(c).equals("+")) {
                if (storeString.length() > 0) {
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
            compoundTable.put(index, parseCompound(storeString.toString(), coefficient));
        }
        return compoundTable;
    }


    /**
     * Removes all characters that are not letters, numbers, parentheses(), and plus signs("+")
     */
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

    /**
     * String formatting of solution
     */
    private static String formatSolution(String originalString, LinkedList<Integer> solutions) {
        String[] arr = originalString.split("\\+");
        StringBuilder s = new StringBuilder();
        String coefficientHandler = "";

        for (int i = 0; i < solutions.size(); i++) {
            if (Character.isDigit(arr[i].charAt(0))) {
                int j = 0;
                while (Character.isDigit(arr[i].charAt(j))) {
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
            } else {
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
        else {
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
                        if (!symbol.isEmpty()) {
                            //Symbol is filled and needs to be dumped
                            int quantity = numString.isEmpty() ? 1 : Integer.parseInt(numString);
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
                        if (parenthesesScaler.isEmpty())
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
                    if (parenthesesScaler.isEmpty())
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
            if (numString.isEmpty()) {
                numString = "1";
            }
            if (!dictionary.containsKey(symbol)) {
                dictionary.put(symbol, Integer.valueOf(numString));
            } else {
                dictionary.put(symbol, Integer.parseInt(numString) + dictionary.get(symbol));
            }
        } else {
            Hashtable<String, Integer> parenthesesParse = parseCompound(paranthesesStoreString.toString(), coefficient);
            if (parenthesesScaler.isEmpty())
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

    // TODO remove
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
        // 16
        if (balance("H2+O2", "H2O+O3").isPresent())
            return "H2+O2 = H2O+O3";
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
