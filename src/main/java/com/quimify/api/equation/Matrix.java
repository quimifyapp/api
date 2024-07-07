package com.quimify.api.equation;

class Matrix {

    private final Fraction[][] matrix;

    // Constructors:

    Matrix(int rows, int columns) {
        this.matrix = new Fraction[rows][columns];
    }

    Matrix(Matrix other) {
        this.matrix = new Fraction[other.rows()][other.columns()];

        for (int row = 0; row < rows(); row++)
            for (int column = 0; column < columns(); column++)
                this.matrix[row][column] = new Fraction(other.get(row, column));
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

    boolean isEmpty() {
        return rows() == 0 || columns() == 0;
    }

    void swapRows(int row1, int row2) {
        Fraction[] oldRow1 = matrix[row1];
        matrix[row1] = matrix[row2];
        matrix[row2] = oldRow1;
    }

    void addRowTo(int addendRow, int destinationRow) {
        for (int j = 0; j < columns(); j++)
            matrix[destinationRow][j] = get(destinationRow, j).plus(get(addendRow, j));
    }

    void multiplyRow(int row, Fraction fraction) {
        for (int j = 0; j < columns(); j++)
            matrix[row][j] = get(row, j).times(fraction);
    }

}
