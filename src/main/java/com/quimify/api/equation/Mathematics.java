package com.quimify.api.equation;

class Mathematics {

    // Internal:

    static int leastCommonMultiple(int a, int b) {
        return (Math.abs(a * b) / greatestCommonDivisor(a, b));
    }

    static int greatestCommonDivisor(int a, int b) {
        if (a == 0 || b == 0)
            return a + b;

        int absNumber1 = Math.abs(a);
        int absNumber2 = Math.abs(b);

        int greater = Math.max(absNumber1, absNumber2);
        int lesser = Math.min(absNumber1, absNumber2);

        return greatestCommonDivisor(greater % lesser, lesser);
    }

    static Fraction[] solve(Matrix matrix) {
        Matrix reducedMatrix = new Matrix(matrix);

        applyGaussJordanElimination(reducedMatrix);

        return solutionsFrom(reducedMatrix);
    }

    // Private:

    private static void applyGaussJordanElimination(Matrix matrix) {
        if (matrix.isEmpty())
            throw new IllegalArgumentException("Can't solve empty matrix.");

        reduceToEchelonForm(matrix);

        for (int column = 0, row = 0; column < matrix.columns() && row < matrix.rows(); column++) {
            if (!matrix.get(row, column).equals(Fraction.ONE))
                continue;

            for (int i = 0; i < row; i++)
                makeZeroIn(matrix, i, column);

            row++;
        }
    }

    private static void reduceToEchelonForm(Matrix matrix) {
        for (int column = 0, row = 0; column < matrix.columns() && row < matrix.rows(); column++) {
            int nonZero = firstNonZeroRowInColumnAfter(matrix, column, row);

            if (nonZero == -1)
                continue;

            int one = firstOneRowInColumnAfter(matrix, column, row);

            if (one == -1) {
                Fraction inverse = matrix.get(nonZero, column).inverse();
                matrix.multiplyRow(nonZero, inverse);
                matrix.swapRows(row, nonZero);
            }
            else matrix.swapRows(row, one);

            for (int i = row + 1; i < matrix.rows(); i++)
                makeZeroIn(matrix, i, column);

            row++;
        }
    }

    private static int firstNonZeroRowInColumnAfter(Matrix matrix, int column, int startRow) {
        for (int row = startRow; row < matrix.rows(); row++)
            if (!matrix.get(row, column).equals(Fraction.ZERO))
                return row;

        return -1;
    }

    private static int firstOneRowInColumnAfter(Matrix matrix, int column, int startRow) {
        for (int row = startRow; row < matrix.rows(); row++)
            if (matrix.get(row, column).equals(Fraction.ONE))
                return row;

        return -1;
    }

    private static void makeZeroIn(Matrix matrix, int row, int column) {
        if (matrix.get(row, column).equals(Fraction.ZERO))
            return;

        Fraction multiple = matrix.get(row, column).negative();
        matrix.multiplyRow(column, multiple);
        matrix.addRowTo(column, row);

        Fraction inverse = matrix.get(column, column).inverse();
        matrix.multiplyRow(column, inverse);
    }

    private static Fraction[] solutionsFrom(Matrix reducedMatrix) {
        Fraction[] solutions = new Fraction[reducedMatrix.columns() - 1];

        for (int row = 0, i = 0; row < reducedMatrix.rows(); row++) {
            Fraction solution = reducedMatrix.get(row, reducedMatrix.columns() - 1);

            if (solution.equals(Fraction.ZERO))
                continue;

            solutions[i++] = solution;
        }

        return solutions;
    }

}
