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

    // TODO take it out
    public void gaussJordanElimination() {
        gaussianElimination();

        int rowIndex = 0;

        for (int columnIndex = 0; columnIndex < columns(); columnIndex++) {
            if (!get(rowIndex, columnIndex).equals(Fraction.one()))
                continue;

            for (int i = 0; i < rowIndex; i++) {
                if (!get(i, columnIndex).equals(Fraction.zero())) {
                    multiplyRow(rowIndex, get(i, columnIndex).negative());
                    addRowTo(rowIndex, i);
                    multiplyRow(rowIndex, get(rowIndex, columnIndex).inverse());
                }
            }

            rowIndex++;

            if (rowIndex >= rows())
                break;
        }
    }

    // Private:

    private boolean isEmpty() {
        return rows() == 0 || columns() == 0;
    }

    private void swapRows(int row1, int row2) {
        Fraction[] oldRow1 = matrix[row1];
        matrix[row1] = matrix[row2];
        matrix[row2] = oldRow1;
    }

    private void addRowTo(int addendRow, int destinationRow) {
        for (int j = 0; j < columns(); j++)
            matrix[destinationRow][j] = get(destinationRow, j).plus(get(addendRow, j));
    }

    private void multiplyRow(int row, Fraction fraction) {
        for (int j = 0; j < columns(); j++)
            matrix[row][j] = get(row, j).times(fraction);
    }

    // TODO take it out
    private void gaussianElimination() {
        if (isEmpty())
            System.out.println("Matrix Error: Cannot solve empty matrix."); // TODO handle appropriately

        int rowIndex = 0;
        for (int columnIndex = 0; columnIndex < columns(); columnIndex++) {
            Integer nonZero = null;
            Integer one = null;

            for (int i = rowIndex; i < rows(); i++) {
                if (nonZero == null && !get(i, columnIndex).equals(Fraction.zero()))
                    nonZero = i;

                if (one == null && get(i, columnIndex).equals(Fraction.one()))
                    one = i;
            }

            if (nonZero != null) {
                if (one != null) {
                    swapRows(rowIndex, one);
                } else {
                    multiplyRow(nonZero, get(nonZero, columnIndex).inverse());
                    swapRows(rowIndex, nonZero);
                }

                for (int i = rowIndex + 1; i < rows(); i++) {
                    if (!get(i, columnIndex).equals(Fraction.zero())) {
                        multiplyRow(rowIndex, get(i, columnIndex).negative());
                        addRowTo(rowIndex, i);
                        multiplyRow(rowIndex, get(rowIndex, columnIndex).inverse());
                    }
                }

                rowIndex++;

                if (rowIndex >= rows())
                    break;
            }
        }
    }

}