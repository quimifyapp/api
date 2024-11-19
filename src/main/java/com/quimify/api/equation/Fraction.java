package com.quimify.api.equation;

import java.util.Objects;

class Fraction {

    private final int numerator;
    private final int denominator;

    // Constants:

    static final Fraction ZERO = new Fraction(0);
    static final Fraction ONE = new Fraction(1);

    // Constructors:

    Fraction(int numerator, int denominator) {
        if (denominator == 0)
            throw new IllegalArgumentException("Denominator can't be zero.");

        int[] simplifiedNumeratorAndDenominator = simplify(numerator, denominator);

        this.numerator = simplifiedNumeratorAndDenominator[0];
        this.denominator = simplifiedNumeratorAndDenominator[1];
    }

    Fraction(int scalar) {
        numerator = scalar;
        denominator = 1;
    }

    // Internal:

    Fraction plus(Fraction other) {
        int newDenominator = Mathematics.calculateLeastCommonMultiple(denominator, other.denominator);
        int newNumerator = rebasedNumerator(newDenominator) + other.rebasedNumerator(newDenominator);

        return new Fraction(newNumerator, newDenominator);
    }

    Fraction times(Fraction other) {
        int newNumerator = numerator * other.numerator;
        int newDenominator = denominator * other.denominator;

        return new Fraction(newNumerator, newDenominator);
    }

    Fraction negative() {
        return new Fraction(-numerator, denominator);
    }

    Fraction inverse() {
        if (numerator == 0)
            throw new IllegalStateException("Fraction zero has no inverse.");

        return new Fraction(denominator, numerator);
    }

    // Private:

    private int[] simplify(int numerator, int denominator) {
        if (denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }

        int greatestCommonDivisor = Mathematics.calculateGreatestCommonDivisor(numerator, denominator);
        numerator /= greatestCommonDivisor;
        denominator /= greatestCommonDivisor;

        return new int[]{numerator, denominator};
    }

    private int rebasedNumerator(int newDenominator) {
        int newNumerator = numerator;
        newNumerator *= newDenominator;
        newNumerator /= denominator;

        return newNumerator;
    }

    // Overridden:

    @Override
    public int hashCode() {
        return Objects.hash(numerator, denominator);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || getClass() != other.getClass())
            return false;

        Fraction otherFraction = (Fraction) other;

        return numerator == otherFraction.numerator && denominator == otherFraction.denominator;
    }

    // Getters:

    int getNumerator() {
        return numerator;
    }

    int getDenominator() {
        return denominator;
    }

}