package com.quimify.api.balancer;

class Matrix {

    private final Fraction[][] matrix;

    // Constructors:

    Matrix(int[][] matrix) {
        this.matrix = new Fraction[matrix.length][matrix[0].length];

        for (int i = 0; i < rows(); i++)
            for (int j = 0; j < columns(); j++)
                this.matrix[i][j] = new Fraction(matrix[i][j]);
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