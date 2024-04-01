package com.quimify.api.balancer;

import org.springframework.stereotype.Component;

// El código está en proceso de adaptación de un programa main a llamada API
// The code is in the process of translation from a main program tu an API call

/**
 * Custom Fraction class with all basic operations(adding, multiplying, negating, simplifying, scaling) performed
 * during gaussian elimination. Fractions are made to be immutable in this class. Note all Fractions are simplified after any operation.
 */
@Component
public class FractionComponent {
    public int numerator;
    public int denominator;

    /**
     * Basic Initialization of a Fraction
     */
    public FractionComponent(int numerator, int denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    /**
     * Scaling of a Fraction that can be used to make a common denominator before adding
     */
    public FractionComponent scaleDenominator(Integer newDenominator) {
        numerator *= Double.valueOf(newDenominator) / (double) this.denominator;
        denominator = newDenominator;
        return new FractionComponent(numerator, denominator);
    }

    /**
     * Simple Function that adds two Fractions
     */
    public static FractionComponent add(FractionComponent f1, FractionComponent f2) {
        //Find LCM of denominator
        int lcm = FractionComponent.lcm(f1.denominator, f2.denominator);
        FractionComponent scaledF1 = f1.scaleDenominator(lcm);
        FractionComponent scaledF2 = f2.scaleDenominator(lcm);
        return FractionComponent.simplify(new FractionComponent(scaledF1.numerator + scaledF2.numerator, lcm));
    }

    /**
     * Simple function that negates a Fraction
     */
    public static FractionComponent negate(FractionComponent f) {
        return new FractionComponent(-f.numerator, f.denominator);
    }

    /**
     * Simple Function that multiplies two Fractions
     */
    public static FractionComponent multiply(FractionComponent f1, FractionComponent f2) {
        int numerator = f1.numerator * f2.numerator;
        int denominator = f1.denominator * f2.denominator;
        return FractionComponent.simplify(new FractionComponent(numerator, denominator));
    }

    /**
     * Simple Function to simplify and standardize(0/5 -> 0/1) Fractions
     */
    public static FractionComponent simplify(FractionComponent f) {
        if (f.denominator < 0) {
            f.numerator *= -1;
            f.denominator *= -1;
        }
        if (f.numerator == 0) {
            return new FractionComponent(0, 1);
        } else {
            int gcd = FractionComponent.gcd(f.numerator, f.denominator);
            return new FractionComponent(f.numerator / gcd, f.denominator / gcd);
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
    public boolean equals(FractionComponent f) {
        return f.numerator == this.numerator && f.denominator == this.denominator;
    }

    /**
     * Simple LCM algorithm based on Euclidean GCD algorithm
     */
    public static int lcm(int number1, int number2) {
        return (Math.abs(number1 * number2) / FractionComponent.gcd(number1, number2));
    }
}