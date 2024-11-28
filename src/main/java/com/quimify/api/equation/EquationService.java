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

@Service
class EquationService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    NotFoundQueryService notFoundQueryService;

    @Autowired
    ErrorService errorService;

    @Autowired
    MetricsService metricsService;

    private static final String equationTooLongError = "La reacción es demasiado larga.";
    private static final String invalidDigitsInReactantsError = "Los reactivos contienen dígitos inválidos.";
    private static final String invalidDigitsInProductsError = "Los productos contienen dígitos inválidos.";
    private static final String invalidParenthesesInReactantsError = "Los reactivos contienen paréntesis inválidos.";
    private static final String invalidParenthesesInProductsError = "Los productos contienen paréntesis inválidos.";
    private static final String mismatchedElementsError = "Los reactivos y los productos no tienen los mismos elementos.";
    private static final String equationNotBalanceableError = "Esta reacción no es balanceable.";

    EquationResult tryBalance(String reactants, String products) {
        EquationResult equationResult;

        String equationForLogs = reactants + " = " + products;

        try {
            equationResult = balance(reactants, products);

            if (!equationResult.isPresent())
                logger.warn("Couldn't calculate \"" + equationForLogs + "\". " + "RESULT: " + equationResult.getError());
        } catch (StackOverflowError error) {
            errorService.log("StackOverflow error", equationForLogs, getClass());
            equationResult = EquationResult.error(equationTooLongError);
        } catch (Exception exception) {
            errorService.log("Exception calculating: " + equationForLogs, exception.toString(), getClass());
            equationResult = EquationResult.notPresent();
        }

        if (!equationResult.isPresent())
            notFoundQueryService.log(equationForLogs, getClass());

        metricsService.balanceEquationQueried(equationResult.isPresent());

        return equationResult;
    }

    // Private:

    private EquationResult balance(String reactantsText, String productsText) {
        List<String> reactantFormulas = getFormulas(reactantsText);
        List<String> productFormulas = getFormulas(productsText);

        if (hasInvalidDigits(reactantFormulas))
            return EquationResult.error(invalidDigitsInReactantsError);

        if (hasInvalidDigits(productFormulas))
            return EquationResult.error(invalidDigitsInProductsError);

        if (hasInvalidParentheses(reactantFormulas))
            return EquationResult.error(invalidParenthesesInReactantsError);

        if (hasInvalidParentheses(productFormulas))
            return EquationResult.error(invalidParenthesesInProductsError);

        return balance(reactantFormulas, productFormulas);
    }

    private List<String> getFormulas(String sumOfFormulas) {
        return Arrays.stream(sumOfFormulas.split(" \\+ ")).collect(Collectors.toList());
    }

    private boolean hasInvalidDigits(List<String> formulas) {
        return formulas.stream().anyMatch(this::hasInvalidDigits);
    }

    private boolean hasInvalidDigits(String formula) {
        return hasLeadingZeros(formula) || hasMisplacedCoefficients(formula);
    }

    private boolean hasLeadingZeros(String formula) {
        return formula.matches(".*(^|\\D)0.*");
    }

    private boolean hasMisplacedCoefficients(String formula) {
        return formula.matches(".*(^|\\(| )\\d.*");
    }

    private boolean hasInvalidParentheses(List<String> formulas) {
        return formulas.stream().anyMatch(this::hasInvalidParentheses);
    }

    private boolean hasInvalidParentheses(String formula) {
        if (formula.contains("()"))
            return true;

        int balance = 0;

        for (char character : formula.toCharArray()) {
            if (character == '(')
                balance++;
            else if (character == ')')
                balance--;

            if (balance < 0)
                return true;
        }

        return balance != 0;
    }

    private EquationResult balance(List<String> reactantFormulas, List<String> productFormulas) {
        List<Formula> reactants = correctAndParseFormulas(reactantFormulas);
        List<Formula> products = correctAndParseFormulas(productFormulas);

        Set<String> reactantsElements = getElementsIn(reactants);
        Set<String> productsElements = getElementsIn(products);

        if (!reactantsElements.equals(productsElements))
            return EquationResult.error(mismatchedElementsError);

        return balance(reactants, products, reactantsElements);
    }

    private List<Formula> correctAndParseFormulas(List<String> formulas) {
        List<String> correctedFormulas = formulas.stream().map(this::correctParentheses).collect(Collectors.toList());
        List<String> uniqueCorrectedFormulas = correctedFormulas.stream().distinct().collect(Collectors.toList());
        return uniqueCorrectedFormulas.stream().map(Formula::new).collect(Collectors.toList());
    }

    private String correctParentheses(String text) {
        final String pattern = "\\((.*)\\)";

        while (text.matches(pattern))
            text = text.replaceAll(pattern, "$1");

        return text;
    }

    private Set<String> getElementsIn(List<Formula> formulas) {
        return formulas.stream().map(Formula::getElements).flatMap(Set::stream).collect(Collectors.toSet());
    }

    private EquationResult balance(List<Formula> reactants, List<Formula> products, Set<String> elements) {
        int[] solution = solve(reactants, products, elements);

        if (meansEquationNotBalanceable(solution))
            return EquationResult.error(equationNotBalanceableError);

        int[] reactantsSolution = Arrays.copyOfRange(solution, 0, reactants.size());
        int[] productsSolution = Arrays.copyOfRange(solution, reactants.size(), solution.length);

        String resultReactants = writeSumOfFormulasWithCoefficients(reactants, reactantsSolution);
        String resultProducts = writeSumOfFormulasWithCoefficients(products, productsSolution);

        return new EquationResult(resultReactants, resultProducts);
    }

    private int[] solve(List<Formula> reactants, List<Formula> products, Set<String> elements) {
        Matrix equations = getReactionEquations(reactants, products, elements);

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
                int molesOfElementInReactant = reactant.getMolesOf(element);

                equations.set(row, column, new Fraction(molesOfElementInReactant));
            }

            for (int column = reactants.size(); column < equations.columns() - 1; column++) {
                Formula product = products.get(column - reactants.size());
                int molesOfElementInProduct = -1 * product.getMolesOf(element);

                equations.set(row, column, new Fraction(molesOfElementInProduct));
            }
        }

        return equations;
    }

    private boolean meansEquationNotBalanceable(int[] solution) {
        return Arrays.stream(solution).anyMatch(element -> element == 0);
    }

    private String writeSumOfFormulasWithCoefficients(List<Formula> formulas, int[] solutions) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < formulas.size(); i++) {
            if (i > 0)
                result.append(" + ");

            if (solutions[i] != 1)
                result.append(solutions[i]);

            result.append(formulas.get(i));
        }

        return result.toString();
    }

}
