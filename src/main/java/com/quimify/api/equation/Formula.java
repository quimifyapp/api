package com.quimify.api.equation;

import java.util.HashMap;
import java.util.Map;

class Formula {

    Map<String, Integer> elementToMoles;

    Formula(String formulaWithOrWithoutMultiplier) {
        String initialDigits = formulaWithOrWithoutMultiplier.replaceAll("\\D.*$", "");
        String formula = formulaWithOrWithoutMultiplier.replaceAll("^" + initialDigits, "");

        Map<String, Integer> elementToMoles = parseFormula(formula);

        Integer multiplier = initialDigits.isEmpty() ? 1 : Integer.parseInt(initialDigits);
        for (String element : elementToMoles.keySet())
            elementToMoles.replace(element, elementToMoles.get(element) * multiplier);

        this.elementToMoles = elementToMoles;
    }

    int molesOf(String element) {
        Integer moles = elementToMoles.get(element);
        return moles != null ? moles : 0;
    }

    // Private:

    private static Map<String, Integer> parseFormula(String formula) {
        int[] index = new int[]{0}; // So it's shared along the stack
        return parseFormula(formula.toCharArray(), index);
    }

    private static Map<String, Integer> parseFormula(char[] formula, int[] index) {
        Map<String, Integer> elementToMoles = new HashMap<>();

        while (index[0] < formula.length) {
            char character = formula[index[0]];

            if (character == ')') {
                index[0]++;
                return elementToMoles;
            }

            if (character == '(') {
                index[0]++;
                Map<String, Integer> nestedCount = parseFormula(formula, index);
                int multiplier = getMultiplier(formula, index);
                for (Map.Entry<String, Integer> entry : nestedCount.entrySet()) {
                    int currentMoles = elementToMoles.getOrDefault(entry.getKey(), 0);
                    int moles = currentMoles + entry.getValue() * multiplier;
                    elementToMoles.put(entry.getKey(), moles);
                }
                continue;
            }

            String element = parseElement(formula, index);
            int count = getMultiplier(formula, index);
            elementToMoles.put(element, elementToMoles.getOrDefault(element, 0) + count);
        }

        return elementToMoles;
    }

    private static String parseElement(char[] formula, int[] index) {
        StringBuilder element = new StringBuilder();

        do {
            element.append(formula[index[0]++]); // Additional characters (lowercase)
        } while (index[0] < formula.length && Character.isLowerCase(formula[index[0]]));

        return element.toString();
    }

    private static int getMultiplier(char[] formula, int[] index) {
        int multiplier = 0;

        while (index[0] < formula.length && Character.isDigit(formula[index[0]]))
            multiplier = multiplier * 10 + (formula[index[0]++] - '0');

        return multiplier == 0 ? 1 : multiplier;
    }

}
