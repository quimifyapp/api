package com.quimify.api.equation;

class Matrix {

    private final Fraction[][] matrix;

    // Constructors:

    Matrix(int rows, int columns) {
        matrix = new Fraction[rows][columns];

        for (int row = 0; row < rows(); row++)
            for (int column = 0; column < columns(); column++)
                matrix[row][column] = Fraction.ZERO;
    }

    Matrix(Matrix other, int rows, int columns) {
        this(rows, columns);

        int rowsToCopy = Math.min(rows, other.rows());
        int columnsToCopy = Math.min(columns, other.columns());

        for (int row = 0; row < rowsToCopy; row++)
            for (int column = 0; column < columnsToCopy; column++)
                matrix[row][column] = other.get(row, column);
    }

    Matrix(Matrix other) {
        this(other, other.rows(), other.columns());
    }

    // Internal:

    int rows() {
        return matrix.length;
    }

    int columns() {
        return matrix[0].length;
    }

    Fraction get(int row, int column) {
        return matrix[row][column];
    }

    void set(int row, int column, Fraction fraction) {
        matrix[row][column] = fraction;
    }

    // Private:

    void swapRows(int row1, int row2) {
        Fraction[] originalRow1 = matrix[row1];
        matrix[row1] = matrix[row2];
        matrix[row2] = originalRow1;
    }

    void addRowTo(int addendRow, int destinationRow) {
        for (int j = 0; j < columns(); j++)
            matrix[destinationRow][j] = get(destinationRow, j).plus(get(addendRow, j));
    }

    void multiplyRow(int row, Fraction factor) {
        for (int j = 0; j < columns(); j++)
            matrix[row][j] = get(row, j).times(factor);
    }

}
