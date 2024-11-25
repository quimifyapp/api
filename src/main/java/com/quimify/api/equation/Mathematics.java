package com.quimify.api.equation;

import java.util.*;

class Mathematics {

    // Internal:

    static Fraction[] calculateAnySolution(Matrix equations) {
        Matrix reducedRowEchelonFormEquations = calculateReducedRowEchelonForm(equations);
        Matrix augmentedEquations = addMissingEquations(reducedRowEchelonFormEquations);
        Matrix reducedRowEchelonFormAugmentedEquations = calculateReducedRowEchelonForm(augmentedEquations);

        return extractOnlyPossibleSolution(reducedRowEchelonFormAugmentedEquations);
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

        for (Set<Integer> indexes : groupCodependentColumns(equations)) {
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

    static int calculateGreatestCommonDivisor(int... numbers) {
        int greatestCommonDivisor = numbers[0];

        for (int i = 1; i < numbers.length; i++)
            greatestCommonDivisor = calculateGreatestCommonDivisor(greatestCommonDivisor, numbers[i]);

        return greatestCommonDivisor;
    }

    // Private:

    private static Matrix calculateReducedRowEchelonForm(Matrix matrix) {
        Matrix reducedRowEchelonForm = new Matrix(matrix);

        for (int pivot = 0; pivot < reducedRowEchelonForm.columns(); pivot++) {
            Integer pivotRow = findFirstNonZeroRowBelowOrAtDiagonalCell(reducedRowEchelonForm, pivot);

            if (pivotRow == null)
                continue;

            reducedRowEchelonForm.swapRows(pivot, pivotRow);

            Fraction pivotInverse = reducedRowEchelonForm.get(pivot, pivot).inverse();
            reducedRowEchelonForm.multiplyRow(pivot, pivotInverse);

            for (int otherRow = 0; otherRow < reducedRowEchelonForm.rows(); otherRow++)
                if (otherRow != pivot)
                    makeZeroInRowAndPivotColumn(reducedRowEchelonForm, otherRow, pivot);
        }

        return reducedRowEchelonForm;
    }

    private static Matrix addMissingEquations(Matrix reducedRowEchelonFormEquations) {
        int rows = reducedRowEchelonFormEquations.columns() - 1;
        int columns = reducedRowEchelonFormEquations.columns();

        Matrix equations = new Matrix(reducedRowEchelonFormEquations, rows, columns);

        for (int row = 0; row < rows; row++)
            if (isAllZerosInRow(equations, row))
                writeIdentityEquationInRow(equations, row);

        return equations;
    }

    private static Fraction[] extractOnlyPossibleSolution(Matrix reducedRowEchelonFormEquations) {
        Fraction[] solution = new Fraction[reducedRowEchelonFormEquations.rows()];

        int lastColumn = reducedRowEchelonFormEquations.columns() - 1;

        for (int row = 0; row < reducedRowEchelonFormEquations.rows(); row++)
            solution[row] = reducedRowEchelonFormEquations.get(row, lastColumn);

        return solution;
    }

    private static Integer findFirstNonZeroRowBelowOrAtDiagonalCell(Matrix matrix, int diagonal) {
        for (int row = diagonal; row < matrix.rows(); row++)
            if (!matrix.get(row, diagonal).equals(Fraction.ZERO))
                return row;

        return null;
    }

    private static void makeZeroInRowAndPivotColumn(Matrix matrix, int row, int pivot) {
        if (matrix.get(row, pivot).equals(Fraction.ZERO))
            return;

        Fraction multiple = matrix.get(row, pivot).negative();
        matrix.multiplyRow(pivot, multiple);
        matrix.addRowTo(pivot, row);

        Fraction inverse = matrix.get(pivot, pivot).inverse();
        matrix.multiplyRow(pivot, inverse);
    }

    private static boolean isAllZerosInRow(Matrix matrix, int row) {
        for (int column = 0; column < matrix.columns(); column++)
            if (!matrix.get(row, column).equals(Fraction.ZERO))
                return false;

        return true;
    }

    private static void writeIdentityEquationInRow(Matrix matrix, int row) {
        matrix.set(row, row, Fraction.ONE);
        matrix.set(row, matrix.columns() - 1, Fraction.ONE);
    }

    private static boolean isAnySolution(Matrix equations, int[] solutionCandidate) {
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

    private static int calculateGreatestCommonDivisor(int a, int b) {
        if (a == 0 || b == 0)
            return a + b;

        int absNumber1 = Math.abs(a);
        int absNumber2 = Math.abs(b);

        int greater = Math.max(absNumber1, absNumber2);
        int lesser = Math.min(absNumber1, absNumber2);

        return calculateGreatestCommonDivisor(greater % lesser, lesser);
    }

    private static List<Set<Integer>> groupCodependentColumns(Matrix matrix) {
        List<Set<Integer>> codependentColumns = new ArrayList<>();

        Matrix reducedEchelonForm = calculateReducedRowEchelonForm(matrix);

        for (int row = 0; row < reducedEchelonForm.rows(); row++)
            for (int column = row + 1; column < reducedEchelonForm.columns(); column++) {
                if (reducedEchelonForm.get(row, column).equals(Fraction.ZERO))
                    continue;

                Set<Integer> columnCodependentGroup = findOrCreateGroupOfColumn(codependentColumns, column);
                columnCodependentGroup.add(row);
            }

        return codependentColumns;
    }

    private static Set<Integer> findOrCreateGroupOfColumn(List<Set<Integer>> columnGroups, int column) {
        Optional<Set<Integer>> existingGroup = columnGroups.stream().filter(set -> set.contains(column)).findAny();

        if (existingGroup.isPresent())
            return existingGroup.get();

        Set<Integer> newGroup = new HashSet<>(Set.of(column));
        columnGroups.add(newGroup);

        return newGroup;
    }

}
