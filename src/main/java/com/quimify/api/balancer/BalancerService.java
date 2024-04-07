package com.quimify.api.balancer;

import com.quimify.api.element.ElementService;
import com.quimify.api.error.ErrorService;

import java.awt.*;
import java.util.*;
import java.util.List;

import com.quimify.api.metrics.MetricsService;
import com.quimify.api.notfoundquery.NotFoundQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class BalancerService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    NotFoundQueryService notFoundQueryService;

    @Autowired
    ErrorService errorService;

    @Autowired
    MetricsService metricsService;

    private String originalReactantsString;
    private String originalProductsString;
    public String backupReactantsString;
    public String backupProductsString;
    public Hashtable<Integer, Hashtable<String, Integer>> reactants;
    public Hashtable<Integer, Hashtable<String, Integer>> products;
    public LinkedList<String> stringReactants;
    public LinkedList<String> stringProducts;
    public MatrixComponent matrix;
    public Hashtable<Integer, LinkedList<Integer>> finalSolution;


    BalancerResult tryBalance(String equation) {
        BalancerResult balancerResult;

        try {
            balancerResult = balance(equation);

            if (!balancerResult.isPresent())
                logger.warn("Couldn't calculate \"" + equation + "\". " + "RESULT: " + balancerResult.getError());
        } catch (StackOverflowError error) {
            errorService.log("StackOverflow error", equation, getClass());
            balancerResult = BalancerResult.error("La ecuación es demasiado larga.");
        } catch (Exception exception) {
            errorService.log("Exception calculating: " + equation, exception.toString(), getClass());
            balancerResult = BalancerResult.error("");
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
    public BalancerResult balance(String equation) {
        if (equation.contains("=")) {
            String[] arr = equation.split("=");
            originalReactantsString = removeUnnecessaryCharacters(arr[0].replace(" ", ""));
            originalProductsString = removeUnnecessaryCharacters(arr[1].replace(" ", ""));
        } else {
            // Algun tipo de error.service diciendo que falta parte de la ecuacion
        }
        String normalizedReactantString = normalizeEquation(originalReactantsString);
        String normalizedProductString = normalizeEquation(originalProductsString);

        this.backupReactantsString = originalReactantsString;
        this.backupProductsString = originalProductsString;

        //Process Input
        List<Object> parseReactants = parseString(normalizedReactantString);
        reactants = (Hashtable<Integer, Hashtable<String, Integer>>) parseReactants.get(0);
        stringReactants = (LinkedList<String>) parseReactants.get(1);

        List<Object> parseProducts = parseString(normalizedProductString);
        products = (Hashtable<Integer, Hashtable<String, Integer>>) parseProducts.get(0);
        stringProducts = (LinkedList<String>) parseProducts.get(1);

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

            this.matrix = new MatrixComponent(intMatrix);
            finalSolution = new Hashtable<>();

        } else {
            // error.service...
            System.out.println("Error: Same elements need to be on both sides of the equation.");
            System.exit(0);
        }

        this.matrix.gaussjordanElimination();
        FractionComponent[] solutions = new FractionComponent[this.matrix.matrix[0].length];

        int j = 0;
        for (int i = 0; i < this.matrix.matrix.length; i++) {
            if (!this.matrix.matrix[i][this.matrix.matrix[i].length - 1].equals(new FractionComponent(0, 1))) {
                solutions[j] = this.matrix.matrix[i][this.matrix.matrix[0].length - 1];
                j++;
            }
        }

        solutions[this.matrix.matrix[0].length - 1] = new FractionComponent(1, 1);
        int lcm = 1;

        for (FractionComponent f : solutions) {
            lcm = FractionComponent.lcm(lcm, f.denominator);
        }
        for (int i = 0; i < solutions.length; i++) {
            solutions[i] = FractionComponent.multiply(new FractionComponent(lcm, 1), solutions[i]);
        }

        finalSolution.put(0, implementSubstitution(Arrays.copyOfRange(solutions, 0, reactants.size())));
        finalSolution.put(1, implementSubstitution(Arrays.copyOfRange(solutions, reactants.size(), solutions.length)));

        return new BalancerResult(true, equation,
                formatSolution(backupReactantsString, finalSolution.get(0)) + " ---> " + formatSolution(backupProductsString, finalSolution.get(1)));
    }
    private static String normalizeEquation(String equation) {
        StringBuilder normalizedEquation = new StringBuilder();
        int coefficient = 1;
        String multiDigitCoefficient = "";
        boolean isCoefficient = true;
        int contador = 0;

        for (int i = 0; i < equation.length(); i++) {
            char character = equation.charAt(i);

            if (Character.isDigit(character) && isCoefficient) {
                while (Character.isDigit(character)){
                    multiDigitCoefficient = multiDigitCoefficient.concat(String.valueOf(character));
                    character = equation.charAt(contador + 1);
                    contador++;
                }
                contador = 0;
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
                        break;
                    case ')':
                        isCoefficient = false;
                        normalizedEquation.append(character);
                        while (Character.isDigit(equation.charAt(i + 1))){
                            character = equation.charAt(i + 1);
                            normalizedEquation.append(character);
                            i++;
                        }
                        break;
                    default:
                        // Apply coefficient to each element or compound
                        isCoefficient = false;
                        if (Character.isDigit(character)) {
                            normalizedEquation.append(coefficient * Character.getNumericValue(character));
                        }
                        else
                            normalizedEquation.append(character);
                        break;
                }
            }
        }

        return normalizedEquation.toString();
    }

    /**
     * Converts FractionComponents into integers for final formatting for either reactant or product side.
     */
    private static LinkedList<Integer> implementSubstitution(FractionComponent[] arr) {
        LinkedList<Integer> finalCoefficients = new LinkedList<>();
        for (FractionComponent f : arr) {
            finalCoefficients.addLast(f.numerator);
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
        return currentString.replaceAll("[^a-zA-Z0-9()+]", "");
    }

    /**
     * String formatting of solution
     */
    private static String formatSolution(String originalString, LinkedList<Integer> solutions) {
        String[] arr = originalString.split("\\+");
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < solutions.size(); i++) {
            s.append(solutions.get(i));
            s.append(arr[i]);
            if (i < solutions.size() - 1)
                s.append(" + ");
        }
        return s.toString();
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

}