package com.quimify.api.equation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Mathematics {

    // Internal:

    static Fraction[] getAnySolution(Matrix equations) {
        Matrix reducedEchelonFormEquations = calculateReducedRowEchelonForm(equations);
        Matrix augmentedEquations = addMissingEquations(reducedEchelonFormEquations);
        Matrix augmentedReducedEchelonFormEquations = calculateReducedRowEchelonForm(augmentedEquations);

        return getOnlyPossibleSolution(augmentedReducedEchelonFormEquations);
    }

    static int[] rescaleIntoIntegers(Fraction[] fractions) {
        int[] denominators = Arrays.stream(fractions).mapToInt(Fraction::getDenominator).toArray();
        int denominatorsLeastCommonMultiple = calculateLeastCommonMultiple(denominators);

        int[] normalizedFractions = new int[fractions.length];

        for (int i = 0; i < fractions.length; i++) {
            Fraction fraction = fractions[i];
            int factor = denominatorsLeastCommonMultiple / fraction.getDenominator();

            normalizedFractions[i] = fraction.getNumerator() * factor;
        }

        return normalizedFractions;
    }

    static int[] findMinimalSolution(Matrix equations, int[] anySolution) {
        int[] smallestSolution = Arrays.copyOf(anySolution, anySolution.length);

        for (List<Integer> indexes : getSubsetsOfNaturalsFromSizeTwoTo(anySolution.length)) {
            int[] values = indexes.stream().mapToInt(index -> anySolution[index]).toArray();
            int greatestCommonDivisor = calculateGreatestCommonDivisor(values);

            if (greatestCommonDivisor <= 1)
                continue;

            int[] smallerCandidateSolution = Arrays.copyOf(smallestSolution, smallestSolution.length);
            for (int index : indexes)
                smallerCandidateSolution[index] /= greatestCommonDivisor;

            if (isAnySolution(equations, smallerCandidateSolution))
                smallestSolution = smallerCandidateSolution;
        }

        return smallestSolution;
    }

    static int calculateLeastCommonMultiple(int... numbers) {
        int leastCommonMultiple = numbers[0];

        for (int i = 1; i < numbers.length; i++)
            leastCommonMultiple = calculateLeastCommonMultiple(leastCommonMultiple, numbers[i]);

        return leastCommonMultiple;
    }

    public static int calculateGreatestCommonDivisor(int... numbers) {
        int greatestCommonDivisor = numbers[0];

        for (int i = 1; i < numbers.length; i++)
            greatestCommonDivisor = calculateGreatestCommonDivisor(greatestCommonDivisor, numbers[i]);

        return greatestCommonDivisor;
    }

    // Private:

    // TODO check if it's accurate, and if it's actually reduced or if it matters
    static private Matrix calculateReducedRowEchelonForm(Matrix matrix) {
        Matrix reducedRowEchelonForm = new Matrix(matrix);

        for (int row = 0, column = 0; column < reducedRowEchelonForm.columns(); column++) {
            Integer pivotRow = findFirstNonZeroRowInColumnAfter(reducedRowEchelonForm, column, row);

            if (pivotRow == null)
                continue;

            reducedRowEchelonForm.swapRows(row, pivotRow);
            Fraction pivotInverse = reducedRowEchelonForm.get(row, column).inverse();
            reducedRowEchelonForm.multiplyRow(row, pivotInverse);

            for (int otherRow = 0; otherRow < reducedRowEchelonForm.rows(); otherRow++)
                if (otherRow != row) // TODO is if needed?
                    makeZeroIn(reducedRowEchelonForm, otherRow, column);

            row++;
        }

        return reducedRowEchelonForm;
    }

    static private Matrix addMissingEquations(Matrix reducedRowEchelonFormEquations) {
        int rows = reducedRowEchelonFormEquations.columns() - 1;
        int columns = reducedRowEchelonFormEquations.columns();

        Matrix equations = new Matrix(reducedRowEchelonFormEquations, rows, columns);

        for (int row = 0; row < rows; row++)
            if (isAllZerosInRow(equations, row))
                addIdentityEquationInRow(equations, row);

        return equations;
    }

    static private Fraction[] getOnlyPossibleSolution(Matrix reducedRowEchelonFormEquations) {
        Fraction[] solution = new Fraction[reducedRowEchelonFormEquations.rows()];

        int lastColumn = reducedRowEchelonFormEquations.columns() - 1;

        for (int row = 0; row < reducedRowEchelonFormEquations.rows(); row++)
            solution[row] = reducedRowEchelonFormEquations.get(row, lastColumn);

        return solution;
    }

    private static Integer findFirstNonZeroRowInColumnAfter(Matrix matrix, int column, int startRow) {
        for (int row = startRow; row < matrix.rows(); row++)
            if (!matrix.get(row, column).equals(Fraction.ZERO))
                return row;

        return null;
    }

    // TODO is name correct?
    private static void makeZeroIn(Matrix matrix, int row, int column) {
        if (matrix.get(row, column).equals(Fraction.ZERO))
            return;

        Fraction multiple = matrix.get(row, column).negative();
        matrix.multiplyRow(column, multiple);
        matrix.addRowTo(column, row);

        Fraction inverse = matrix.get(column, column).inverse();
        matrix.multiplyRow(column, inverse);
    }

    private static boolean isAllZerosInRow(Matrix matrix, int row) {
        for (int column = 0; column < matrix.columns(); column++)
            if (!matrix.get(row, column).equals(Fraction.ZERO))
                return false;

        return true;
    }

    private static void addIdentityEquationInRow(Matrix matrix, int row) {
        matrix.set(row, row, Fraction.ONE);
        matrix.set(row, matrix.columns() - 1, Fraction.ONE);
    }

    static private boolean isAnySolution(Matrix equations, int[] solutionCandidate) {
        for (int row = 0; row < equations.rows(); row++) {
            Fraction equationEvaluation = Fraction.ZERO;

            for (int column = 0; column < equations.columns() - 1; column++) {
                Fraction coefficient = equations.get(row, column);
                Fraction variable = new Fraction(solutionCandidate[column]);

                equationEvaluation = equationEvaluation.plus(coefficient.times(variable));
            }

            if (!equationEvaluation.equals(equations.get(row, equations.columns() - 1)))
                return false;
        }

        return true;
    }

    private static int calculateLeastCommonMultiple(int a, int b) {
        return (Math.abs(a * b) / calculateGreatestCommonDivisor(a, b));
    }

    static int calculateGreatestCommonDivisor(int a, int b) {
        if (a == 0 || b == 0)
            return a + b;

        int absNumber1 = Math.abs(a);
        int absNumber2 = Math.abs(b);

        int greater = Math.max(absNumber1, absNumber2);
        int lesser = Math.min(absNumber1, absNumber2);

        return calculateGreatestCommonDivisor(greater % lesser, lesser);
    }

    public static List<List<Integer>> getSubsetsOfNaturalsFromSizeTwoTo(int maximumSubsetSize) {
        List<List<Integer>> subsetsOfNaturals = new ArrayList<>();

        for (int i = 2; i <= maximumSubsetSize - 1; i++)
            generateSubsetCombinations(subsetsOfNaturals, new ArrayList<>(), 0, maximumSubsetSize, i);

        return subsetsOfNaturals;
    }

    private static void generateSubsetCombinations(List<List<Integer>> result, List<Integer> currentSubset, int start,
                                                   int maximumSubsetSize, int size) {
        if (currentSubset.size() == size) {
            result.add(new ArrayList<>(currentSubset));
            return;
        }

        for (int i = start; i < maximumSubsetSize; i++) {
            currentSubset.add(i);
            generateSubsetCombinations(result, currentSubset, i + 1, maximumSubsetSize, size);
            currentSubset.remove(currentSubset.size() - 1);
        }
    }

}
