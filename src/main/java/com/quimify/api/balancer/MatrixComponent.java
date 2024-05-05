package com.quimify.api.balancer;

class MatrixComponent {
    public Fraction[][] matrix;
    public boolean print=true;

    /**
     * Matrix Initializer that converts int array into fraction array
     */
    public MatrixComponent(int[][] matrix){
        this.matrix = new Fraction[matrix.length][matrix[0].length];
        for(int j=0; j<matrix.length; j++) {
            int[] row = matrix[j];
            Fraction[] fractionRow = new Fraction[row.length];
            for(int i=0; i<fractionRow.length; i++) {
                fractionRow[i] = new Fraction(row[i], 1);
            }
            this.matrix[j] = fractionRow;
        }
    }

    public MatrixComponent(Fraction[][] matrix){
        this.matrix = matrix;
    }

    /**
     * Simple Function that swaps rows
     */
    public void rowSwap(int row1, int row2){
        Fraction[] temp = this.matrix[row1-1];
        this.matrix[row1-1] = this.matrix[row2-1];
        this.matrix[row2-1] = temp;
    }

    /**
     * Simple Function that adds 2 rows
     */
    public void rowAddition(int row1, int row2){
        Fraction[] addedRow = new Fraction[this.matrix[row1-1].length];
        for(int i=0; i<this.matrix[row1-1].length; i++){
            addedRow[i] = Fraction.add(this.matrix[row1-1][i], this.matrix[row2-1][i]);
        }
        this.matrix[row2-1] = addedRow;
    }

    /**
     * Simple Function that multiplies 2 rows
     */
    public void rowMultiplication(Fraction scalar, int row){
        for(int i=0; i<this.matrix[row-1].length; i++){
            Fraction f = this.matrix[row-1][i];
            this.matrix[row-1][i] = Fraction.multiply(f, scalar);
        }
    }

    /**
     * Custom Implementation of a Gaussian Elimination Algorithm. Note matrix does not need to be square.
     */
    public void gaussianElimination() {
        if(this.isEmpty()){
            System.out.println("Matrix Error: Cannot solve empty matrix.");
        }

        int rowIndex = 0;
        for(int columnIndex = 0; columnIndex < this.matrix[0].length; columnIndex++){
            Integer nonZero = null;
            Integer one = null;
            for(int i = rowIndex; i < this.matrix.length; i++){
                if(nonZero == null && !this.matrix[i][columnIndex].equals(new Fraction(0,1 ))){
                    nonZero = i;
                }
                if(one == null && this.matrix[i][columnIndex].equals(new Fraction(1,1 ))){
                    one = i;
                }
            }
            if(nonZero != null){
                if(one != null){
                    this.rowSwap(rowIndex+1, one+1);
                }else{
                    this.rowMultiplication(new Fraction(this.matrix[nonZero][columnIndex].getDenominator(), this.matrix[nonZero][columnIndex].getNumerator()), nonZero+1);
                    this.rowSwap(rowIndex+1, nonZero+1);
                }
                if(this.print){System.out.println(this.toString());}
                for(int i = rowIndex + 1; i < this.matrix.length; i++){
                    if(!this.matrix[i][columnIndex].equals(new Fraction(0, 1))){
                        this.rowMultiplication(Fraction.negate(this.matrix[i][columnIndex]), rowIndex+1);
                        this.rowAddition(rowIndex + 1, i + 1);
                        this.rowMultiplication(new Fraction(this.matrix[rowIndex][columnIndex].getDenominator(), this.matrix[rowIndex][columnIndex].getNumerator()), rowIndex+1);
                        if(this.print){System.out.println(this.toString());}
                    }
                }
                rowIndex++;
                if(rowIndex >= this.matrix.length) {
                    break;
                }
            }
        }
    }

    public void gaussjordanElimination() {
        this.gaussianElimination();
        int rowIndex = 0;

        for(int columnIndex = 0; columnIndex < this.matrix[0].length; columnIndex++){
            if(this.matrix[rowIndex][columnIndex].equals(new Fraction(1,1))) {
                for (int i = 0; i < rowIndex; i++) {
                    if(!this.matrix[i][columnIndex].equals(new Fraction(0, 1))){
                        this.rowMultiplication(Fraction.negate(this.matrix[i][columnIndex]), rowIndex + 1);
                        this.rowAddition(rowIndex + 1, i + 1);
                        this.rowMultiplication(new Fraction(this.matrix[rowIndex][columnIndex].getDenominator(), this.matrix[rowIndex][columnIndex].getNumerator()), rowIndex + 1);
                        if (this.print) {System.out.println(this.toString());}
                    }
                }
                rowIndex++;
                if(rowIndex >= this.matrix.length) {
                    break;
                }
            }
        }
    }

    public boolean isEmpty() {
        if(this.matrix.length == 0){
            return true;
        }else{
            if(this.matrix[0].length == 0){
                return true;
            }
        }
        return false;
    }

    public static MatrixComponent multiply(MatrixComponent matrix1, MatrixComponent matrix2) {
        if(matrix1.matrix[0].length != matrix2.matrix.length){
            System.out.println("Multiplication Error: Matrix Multiplication not computable due to dimensions!");
        }
        Fraction[][] productArr = new Fraction[matrix1.matrix.length][matrix2.matrix[0].length];
        for(int i=0; i<matrix1.matrix.length; i++){
            for(int j=0; j<matrix2.matrix[0].length; j++){
                Fraction sum = new Fraction(0, 1);
                for(int k=0; k<matrix2.matrix.length; k++){
                    sum= Fraction.add(sum, Fraction.multiply(matrix1.matrix[i][k], matrix2.matrix[k][j]));
                }
                productArr[i][j] = sum;
            }
        }
        return new MatrixComponent(productArr);
    }

    public static MatrixComponent multiply(Fraction f, MatrixComponent m){
        Fraction[][] product = new Fraction[m.matrix.length][m.matrix[0].length];
        for(int i=0; i<m.matrix.length; i++){
            for(int j=0; j<m.matrix[0].length; j++){
                product[i][j]= Fraction.multiply(m.matrix[i][j], f);
            }
        }
        return new MatrixComponent(product);
    }

    public static MatrixComponent add(MatrixComponent matrix1, MatrixComponent matrix2) {
        if(matrix1.matrix.length != matrix2.matrix.length || matrix1.matrix[0].length != matrix2.matrix[0].length){
            System.out.println("Addition Error: Matrix Addition not computable due to dimensions!");
        }
        Fraction[][] sum = new Fraction[matrix1.matrix.length][matrix1.matrix[0].length];
        for(int i=0; i<matrix1.matrix.length; i++){
            for(int j=0; j<matrix1.matrix[0].length; j++){
                sum[i][j]= Fraction.add(matrix1.matrix[i][j], matrix2.matrix[i][j]);
            }
        }
        return new MatrixComponent(sum);
    }

    public static MatrixComponent negate(MatrixComponent matrix){
        return MatrixComponent.multiply(new Fraction(-1, 1), matrix);
    }



}