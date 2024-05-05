package com.quimify.api.equation;


class Mathematics {

    // Internal:

    static int leastCommonMultiple(int number1, int number2) {
        return (Math.abs(number1 * number2) / greatestCommonDivisor(number1, number2));
    }

    static int greatestCommonDivisor(int number1, int number2) {
        if (number1 == 0 || number2 == 0)
            return number1 + number2;

        int absNumber1 = Math.abs(number1);
        int absNumber2 = Math.abs(number2);

        int greater = Math.max(absNumber1, absNumber2);
        int lesser = Math.min(absNumber1, absNumber2);

        return greatestCommonDivisor(greater % lesser, lesser);
    }

    // TODO rewrite this method from the internet
    static void gaussJordanElimination(Matrix matrix) {
        gaussianElimination(matrix);

        int rowIndex = 0;

        for (int columnIndex = 0; columnIndex < matrix.columns(); columnIndex++) {
            if (!matrix.get(rowIndex, columnIndex).equals(Fraction.one()))
                continue;

            for (int i = 0; i < rowIndex; i++) {
                if (!matrix.get(i, columnIndex).equals(Fraction.zero())) {
                    matrix.multiplyRow(rowIndex, matrix.get(i, columnIndex).negative());
                    matrix.addRowTo(rowIndex, i);
                    matrix.multiplyRow(rowIndex, matrix.get(rowIndex, columnIndex).inverse());
                }
            }

            rowIndex++;

            if (rowIndex >= matrix.rows())
                break;
        }
    }

    // Private:

    // TODO rewrite this method from the internet
    private static void gaussianElimination(Matrix matrix) {
        if (matrix.isEmpty())
            throw new IllegalArgumentException("Can't solve empty matrix.");

        int rowIndex = 0;
        for (int columnIndex = 0; columnIndex < matrix.columns(); columnIndex++) {
            Integer nonZero = null;
            Integer one = null;

            for (int i = rowIndex; i < matrix.rows(); i++) {
                if (nonZero == null && !matrix.get(i, columnIndex).equals(Fraction.zero()))
                    nonZero = i;

                if (one == null && matrix.get(i, columnIndex).equals(Fraction.one()))
                    one = i;
            }

            if (nonZero != null) {
                if (one != null) {
                    matrix.swapRows(rowIndex, one);
                } else {
                    matrix.multiplyRow(nonZero, matrix.get(nonZero, columnIndex).inverse());
                    matrix.swapRows(rowIndex, nonZero);
                }

                for (int i = rowIndex + 1; i < matrix.rows(); i++) {
                    if (!matrix.get(i, columnIndex).equals(Fraction.zero())) {
                        matrix.multiplyRow(rowIndex, matrix.get(i, columnIndex).negative());
                        matrix.addRowTo(rowIndex, i);
                        matrix.multiplyRow(rowIndex, matrix.get(rowIndex, columnIndex).inverse());
                    }
                }

                rowIndex++;

                if (rowIndex >= matrix.rows())
                    break;
            }
        }
    }

}
