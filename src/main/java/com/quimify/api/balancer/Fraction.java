package com.quimify.api.balancer;

import java.util.Objects;

class Fraction {

    private int numerator;
    private int denominator;

    // Constructors:

    Fraction(int numerator, int denominator) {
        if (denominator == 0)
            throw new IllegalArgumentException("Denominator can't be zero.");

        this.numerator = numerator;
        this.denominator = denominator;

        simplify();
    }

    Fraction(int scalar) {
        this.numerator = scalar;
        this.denominator = 1;
    }

    static Fraction zero() {
        return new Fraction(0);
    }

    static Fraction one() {
        return new Fraction(1);
    }

    private Fraction() {}

    private static Fraction unsimplified(int numerator, int denominator) {
        Fraction unsimplified = new Fraction();

        unsimplified.numerator = numerator;
        unsimplified.denominator = denominator;

        return unsimplified;
    }

    // Internal:

    Fraction plus(Fraction other) {
        int newDenominator = Fraction.leastCommonMultiple(denominator, other.denominator);
        int newNumerator = rebasedNumerator(newDenominator) + other.rebasedNumerator(newDenominator);

        return new Fraction(newNumerator, newDenominator);
    }

    Fraction times(Fraction other) {
        int newNumerator = numerator * other.numerator;
        int newDenominator = denominator * other.denominator;

        return new Fraction(newNumerator, newDenominator);
    }

    Fraction negative() {
        return unsimplified(-numerator, denominator);
    }

    Fraction inverse() {
        if (numerator == 0)
            throw new IllegalStateException("Fraction zero has no inverse.");

        return unsimplified(denominator, numerator);
    }

    // TODO take outside
    static int leastCommonMultiple(int number1, int number2) {
        return (Math.abs(number1 * number2) / Fraction.greatestCommonDivisor(number1, number2));
    }

    // Private:

    private void simplify() {
        if (denominator < 0) {
            numerator *= -1;
            denominator *= -1;
        }

        int greatestCommonDivisor = Fraction.greatestCommonDivisor(numerator, denominator);
        numerator /= greatestCommonDivisor;
        denominator /= greatestCommonDivisor;
    }

    private int rebasedNumerator(Integer newDenominator) {
        int newNumerator = numerator;
        newNumerator *= newDenominator;
        newNumerator /= denominator;

        return newNumerator;
    }

    // TODO take outside
    private static int greatestCommonDivisor(int number1, int number2) {
        if (number1 == 0 || number2 == 0)
            return number1 + number2;

        int absNumber1 = Math.abs(number1);
        int absNumber2 = Math.abs(number2);

        int greater = Math.max(absNumber1, absNumber2);
        int lesser = Math.min(absNumber1, absNumber2);

        return greatestCommonDivisor(greater % lesser, lesser);
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