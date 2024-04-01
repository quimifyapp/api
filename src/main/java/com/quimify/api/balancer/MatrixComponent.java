package com.quimify.api.balancer;

import org.springframework.stereotype.Component;

// El código está en proceso de adaptación de un programa main a llamada API
// The code is in the process of translation from a main program tu an API call
@Component
public class MatrixComponent {
    public FractionComponent[][] matrix;
    public boolean print=true;

    /**
     * Matrix Initializer that converts int array into fraction array
     */
    public MatrixComponent(int[][] matrix){
        this.matrix = new FractionComponent[matrix.length][matrix[0].length];
        for(int j=0; j<matrix.length; j++) {
            int[] row = matrix[j];
            FractionComponent[] fractionRow = new FractionComponent[row.length];
            for(int i=0; i<fractionRow.length; i++) {
                fractionRow[i] = new FractionComponent(row[i], 1);
            }
            this.matrix[j] = fractionRow;
        }
    }

    public MatrixComponent(boolean augment){
        System.out.println("Enter Size of Matrix" + (augment? " without Augmented Side: ": ""));
        System.out.print("Columns: ");
        int dimension1=sc.nextInt();
        System.out.print("Rows: ");
        int dimension2=sc.nextInt();
        System.out.println("Enter matrix" + (augment?" with augmented matrix. In other words, get all equations into x+y+z+...=3 form.": "."));
        this.matrix = new FractionComponent[dimension1][augment? dimension2+1: dimension2];
        for(int i=0; i<dimension1*((augment? dimension2+1: dimension2)); i++){
            int position1 = i/(augment? dimension2+1: dimension2);
            int position2 = i%(augment? dimension2+1: dimension2);
            System.out.println("\nPosition ("+(position1+1)+", "+(position2+1)+")");
            System.out.print("Fraction: ");
            String fractionString = sc.next();
            Integer numerator = null;
            Integer denominator = null;
            if(fractionString.contains("/")){
                String[] splitString = fractionString.split("/");
                numerator = Integer.parseInt(splitString[0]);
                denominator = Integer.parseInt(splitString[1]);
            }else{
                numerator = Integer.parseInt(fractionString);
                denominator = 1;
            }
            this.matrix[position1][position2] = new FractionComponent(numerator, denominator);
        }
    }

    public MatrixComponent(FractionComponent[][] matrix){
        this.matrix = matrix;
    }

    /**
     * Simple Function that swaps rows
     * @param row1 Row Index 1
     * @param row2 Row Index 2
     */
    public void rowSwap(int row1, int row2){
        FractionComponent[] temp = this.matrix[row1-1];
        this.matrix[row1-1] = this.matrix[row2-1];
        this.matrix[row2-1] = temp;
    }

    /**
     * Simple Function that adds 2 rows
     * @param row1 Row Index 1
     * @param row2 Row Index 2 (Result is stored in this row)
     */
    public void rowAddition(int row1, int row2){
        FractionComponent[] addedRow = new FractionComponent[this.matrix[row1-1].length];
        for(int i=0; i<this.matrix[row1-1].length; i++){
            addedRow[i] = FractionComponent.add(this.matrix[row1-1][i], this.matrix[row2-1][i]);
        }
        this.matrix[row2-1] = addedRow;
    }

    /**
     * Simple Function that multiplies 2 rows
     * @param scalar Fraction scalar
     * @param row Row Index
     */
    public void rowMultiplication(FractionComponent scalar, int row){
        for(int i=0; i<this.matrix[row-1].length; i++){
            FractionComponent f = this.matrix[row-1][i];
            this.matrix[row-1][i] = FractionComponent.multiply(f, scalar);
        }
    }

    /**
     * Custom Implementation of a Gaussian Elimination Algorithm. Note matrix does not need to be square.
     */
    public void gaussianElimination() throws MatrixError {
        if(this.isEmpty()){
            throw new MatrixError("Matrix Error: Cannot solve empty matrix.");
        }
        int rowIndex = 0;
        for(int columnIndex = 0; columnIndex < this.matrix[0].length; columnIndex++){
            Integer nonZero = null;
            Integer one = null;
            for(int i = rowIndex; i < this.matrix.length; i++){
                if(nonZero == null && !this.matrix[i][columnIndex].equals(new FractionComponent(0,1 ))){
                    nonZero = i;
                }
                if(one == null && this.matrix[i][columnIndex].equals(new FractionComponent(1,1 ))){
                    one = i;
                }
            }
            if(nonZero != null){
                if(one != null){
                    this.rowSwap(rowIndex+1, one+1);
                }else{
                    this.rowMultiplication(new FractionComponent(this.matrix[nonZero][columnIndex].denominator, this.matrix[nonZero][columnIndex].numerator), nonZero+1);
                    this.rowSwap(rowIndex+1, nonZero+1);
                }
                if(this.print){System.out.println(this.toString());}
                for(int i = rowIndex + 1; i < this.matrix.length; i++){
                    if(!this.matrix[i][columnIndex].equals(new FractionComponent(0, 1))){
                        this.rowMultiplication(FractionComponent.negate(this.matrix[i][columnIndex]), rowIndex+1);
                        this.rowAddition(rowIndex + 1, i + 1);
                        this.rowMultiplication(new FractionComponent(this.matrix[rowIndex][columnIndex].denominator, this.matrix[rowIndex][columnIndex].numerator), rowIndex+1);
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

    public void gaussianWithBackSubstitution(){
        try {
            this.gaussianElimination();
        } catch (MatrixError matrixError) {
            matrixError.printStackTrace();
            System.exit(0);
        }
        AlgebraEquation.setSize(this.matrix[0].length+1);
        for(int i=this.matrix.length-1; i>=0; i--) {
            //Iterating through reverse
            AlgebraEquation newAlgebraEquation = new AlgebraEquation(this.matrix[i]);
        }
    }

    public void gaussjordanElimination() {
        try {
            this.gaussianElimination();
        } catch (MatrixError matrixError) {
            matrixError.printStackTrace();
            System.exit(0);
        }
        int rowIndex = 0;
        for(int columnIndex = 0; columnIndex < this.matrix[0].length; columnIndex++){
            if(this.matrix[rowIndex][columnIndex].equals(new FractionComponent(1,1))) {
                for (int i = 0; i < rowIndex; i++) {
                    if(!this.matrix[i][columnIndex].equals(new FractionComponent(0, 1))){
                        this.rowMultiplication(FractionComponent.negate(this.matrix[i][columnIndex]), rowIndex + 1);
                        this.rowAddition(rowIndex + 1, i + 1);
                        this.rowMultiplication(new FractionComponent(this.matrix[rowIndex][columnIndex].denominator, this.matrix[rowIndex][columnIndex].numerator), rowIndex + 1);
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

    public FractionComponent determinant() throws MatrixError{
        if(!this.isSquare()){
            throw new MatrixError("Determinant Error: Matrix is not square!");
        }
        if(this.matrix.length==2 && this.matrix[0].length==2){
            return FractionComponent.add(FractionComponent.multiply(this.matrix[0][0], this.matrix[1][1]), FractionComponent.negate(FractionComponent.multiply(this.matrix[1][0], this.matrix[0][1])));
        }else{
            FractionComponent determinant = new FractionComponent(0,1);
            for(int i=0; i<this.matrix.length; i++){
                FractionComponent component = this.matrix[i][0];
                FractionComponent[][] subArr = new FractionComponent[this.matrix.length-1][this.matrix[0].length-1];
                int insertIndex=0;
                for(int i1=0; i1<this.matrix.length; i1++){
                    for(int i2=0; i2<this.matrix[i1].length; i2++){
                        if(i != i1 && i2 != 0){
                            subArr[insertIndex/(this.matrix.length-1)][insertIndex%(this.matrix[0].length-1)]=this.matrix[i1][i2];
                            insertIndex++;
                        }
                    }
                }
                MatrixComponent subMatrix = new MatrixComponent(subArr);
                component=FractionComponent.multiply(component, subMatrix.determinant());
                if(i%2==1){component=FractionComponent.negate(component);}
                determinant=FractionComponent.add(determinant, component);
            }
            return determinant;
        }
    }

    public MatrixComponent inverse() throws MatrixError {
        if(!this.isSquare()){
            throw new MatrixError("Inverse Error: Matrix is not square!");
        }
        FractionComponent[][] solutionArr = new FractionComponent[this.matrix.length][this.matrix.length*2];
        for(int i=0; i<this.matrix.length; i++){
            for(int j=0; j<this.matrix.length*2; j++){
                if(j<this.matrix.length){
                    solutionArr[i][j]=this.matrix[i][j];
                }else{
                    solutionArr[i][j]=(j-this.matrix.length==i? new FractionComponent(1, 1): new FractionComponent(0, 1));
                }
            }
        }
        MatrixComponent solutionMatrix = new MatrixComponent(solutionArr);
        solutionMatrix.gaussjordanElimination();
        FractionComponent[][] inverseArr = new FractionComponent[this.matrix.length][this.matrix.length];
        for(int i=0; i<this.matrix.length; i++){
            for(int j=this.matrix.length; j<this.matrix.length*2; j++){
                inverseArr[i][j-this.matrix.length] = solutionMatrix.matrix[i][j];
            }
        }
        return new MatrixComponent(inverseArr);
    }

    public boolean isSquare(){return this.matrix.length==this.matrix[0].length;}

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

    public static MatrixComponent add(MatrixComponent matrix1, MatrixComponent matrix2) throws MatrixError{
        if(matrix1.matrix.length != matrix2.matrix.length || matrix1.matrix[0].length != matrix2.matrix[0].length){
            throw new MatrixError("Addition Error: Matrix Addition not computable due to dimensions!");
        }
        FractionComponent[][] sum = new FractionComponent[matrix1.matrix.length][matrix1.matrix[0].length];
        for(int i=0; i<matrix1.matrix.length; i++){
            for(int j=0; j<matrix1.matrix[0].length; j++){
                sum[i][j]=FractionComponent.add(matrix1.matrix[i][j], matrix2.matrix[i][j]);
            }
        }
        return new MatrixComponent(sum);
    }

    public static MatrixComponent multiply(FractionComponent f, MatrixComponent m){
        FractionComponent[][] product = new FractionComponent[m.matrix.length][m.matrix[0].length];
        for(int i=0; i<m.matrix.length; i++){
            for(int j=0; j<m.matrix[0].length; j++){
                product[i][j]=FractionComponent.multiply(m.matrix[i][j], f);
            }
        }
        return new MatrixComponent(product);
    }

    public static MatrixComponent negate(MatrixComponent matrix){
        return MatrixComponent.multiply(new FractionComponent(-1, 1), matrix);
    }

    public static MatrixComponent multiply(MatrixComponent matrix1, MatrixComponent matrix2) throws MatrixError {
        if(matrix1.matrix[0].length != matrix2.matrix.length){
            throw new MatrixError("Multiplication Error: Matrix Multiplication not computable due to dimensions!");
        }
        FractionComponent[][] productArr = new FractionComponent[matrix1.matrix.length][matrix2.matrix[0].length];
        for(int i=0; i<matrix1.matrix.length; i++){
            for(int j=0; j<matrix2.matrix[0].length; j++){
                FractionComponent sum = new FractionComponent(0, 1);
                for(int k=0; k<matrix2.matrix.length; k++){
                    sum=FractionComponent.add(sum, FractionComponent.multiply(matrix1.matrix[i][k], matrix2.matrix[k][j]));
                }
                productArr[i][j] = sum;
            }
        }
        return new MatrixComponent(productArr);
    }

}