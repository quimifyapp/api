package com.quimify.api.balancer;

/**
 * Custom Fraction class with all basic operations(adding, multiplying, negating, simplifying, scaling) performed
 * during gaussian elimination. Fractions are made to be immutable in this class. Note all Fractions are simplified after any operation.
 */

class Fraction {
    private int numerator;
    private int denominator;

    /**
     * Basic Initialization of a Fraction
     */
    public Fraction(int numerator, int denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    /**
     * Scaling of a Fraction that can be used to make a common denominator before adding
     */
    public Fraction scaleDenominator(Integer newDenominator) {
        numerator *= Double.valueOf(newDenominator) / (double) this.denominator;
        denominator = newDenominator;
        return new Fraction(numerator, denominator);
    }

    /**
     * Simple Function that adds two Fractions
     */
    public static Fraction add(Fraction f1, Fraction f2) {
        //Find LCM of denominator
        int lcm = Fraction.lcm(f1.denominator, f2.denominator);
        Fraction scaledF1 = f1.scaleDenominator(lcm);
        Fraction scaledF2 = f2.scaleDenominator(lcm);
        return Fraction.simplify(new Fraction(scaledF1.numerator + scaledF2.numerator, lcm));
    }

    /**
     * Simple function that negates a Fraction
     */
    public static Fraction negate(Fraction f) {
        return new Fraction(-f.numerator, f.denominator);
    }

    /**
     * Simple Function that multiplies two Fractions
     */
    public static Fraction multiply(Fraction f1, Fraction f2) {
        int numerator = f1.numerator * f2.numerator;
        int denominator = f1.denominator * f2.denominator;
        return Fraction.simplify(new Fraction(numerator, denominator));
    }

    /**
     * Simple Function to simplify and standardize(0/5 -> 0/1) Fractions
     */
    public static Fraction simplify(Fraction f) {
        if (f.denominator < 0) {
            f.numerator *= -1;
            f.denominator *= -1;
        }
        if (f.numerator == 0) {
            return new Fraction(0, 1);
        } else {
            int gcd = Fraction.gcd(f.numerator, f.denominator);
            return new Fraction(f.numerator / gcd, f.denominator / gcd);
        }
    }

    /**
     * Simple Euclidean GCD algorithm
     */
    private static int gcd(int number1, int number2) {
        if (number1 == 0 || number2 == 0) {
            return number1 + number2;
        } else {
            int absNumber1 = Math.abs(number1);
            int absNumber2 = Math.abs(number2);
            int biggerValue = Math.max(absNumber1, absNumber2);
            int smallerValue = Math.min(absNumber1, absNumber2);
            return gcd(biggerValue % smallerValue, smallerValue);
        }
    }

    /**
     * Comparison of 2 Fractions
     */
    public boolean equals(Fraction f) {
        return f.numerator == this.numerator && f.denominator == this.denominator;
    }

    /**
     * Simple LCM algorithm based on Euclidean GCD algorithm
     */
    public static int lcm(int number1, int number2) {
        return (Math.abs(number1 * number2) / Fraction.gcd(number1, number2));
    }

    // Getters:


    int getNumerator() {
        return numerator;
    }

    int getDenominator() {
        return denominator;
    }

}