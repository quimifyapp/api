package com.quimify.api.equation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EquationServiceTest {

    @Autowired
    private EquationService equationService;

    @Test
    void shouldBalanceEquationCorrectly() {
        EquationResult result = equationService.tryBalance("NaCl + PO", "Na + Cl + P + O", "sp");
        assertTrue(result.isPresent());
        assertEquals("NaCl + PO", result.getBalancedReactants());
        assertEquals("Na + Cl + P + O", result.getBalancedProducts());
    }

    @Test
    void shouldBalanceEquationWithParallelFormulasCorrectly() {
        EquationResult result = equationService.tryBalance("H2S + O4", "H + S + O", "sp");
        assertTrue(result.isPresent());
        assertEquals("H2S + O4", result.getBalancedReactants());
        assertEquals("2H + S + 4O", result.getBalancedProducts());
    }

    @Test
    void shouldBalanceEquationWithRepeatedFormulasCorrectly() {
        EquationResult result = equationService.tryBalance("HO + Cl + Cl", "H + O + H + Cl", "sp");
        assertTrue(result.isPresent());
        assertEquals("HO + Cl", result.getBalancedReactants());
        assertEquals("H + O + Cl", result.getBalancedProducts());
    }

    @Test
    void shouldBalanceEquationWithFormulasSurroundedByParenthesesCorrectly() {
        EquationResult result = equationService.tryBalance("Al(OH)3 + ((H2CO3))", "Al2(CO3)3 + H2O", "sp");
        assertTrue(result.isPresent());
        assertEquals("2Al(OH)3 + 3H2CO3", result.getBalancedReactants());
        assertEquals("Al2(CO3)3 + 6H2O", result.getBalancedProducts());
    }

    @Test
    void shouldNotBalanceEquationWithZeroDigitsInReactants() {
        EquationResult result = equationService.tryBalance("Fe + H0Cl", "FeCl2 + H", "sp");
        assertFalse(result.isPresent());
    }

    @Test
    void shouldNotBalanceEquationWithZeroDigitsInProducts() {
        EquationResult result = equationService.tryBalance("Fe + HCl", "Fe0Cl2 + H", "sp");
        assertFalse(result.isPresent());
    }

    @Test
    void shouldNotBalanceEquationWithLeadingZeroDigitsInReactants() {
        EquationResult result = equationService.tryBalance("NH40H + H3PO4", "(NH4)3PO4 + H2O", "sp");
        assertFalse(result.isPresent());
    }

    @Test
    void shouldNotBalanceEquationWithLeadingZeroDigitsInProducts() {
        EquationResult result = equationService.tryBalance("NH4H + H3PO4", "(NH4)3P04 + H2O", "sp");
        assertFalse(result.isPresent());
    }

    @Test
    void shouldNotBalanceEquationWithCoefficientsInReactants() {
        EquationResult result = equationService.tryBalance("2KClO3", "KCl + O2", "sp");
        assertFalse(result.isPresent());
    }

    @Test
    void shouldNotBalanceEquationWithCoefficientsInProducts() {
        EquationResult result = equationService.tryBalance("KClO3", "KCl + 3O2", "sp");
        assertFalse(result.isPresent());
    }

    @Test
    void shouldNotBalanceEquationWithEmptyParenthesesInReactants() {
        EquationResult result = equationService.tryBalance("KCl()O3", "KCl + 3O2", "sp");
        assertFalse(result.isPresent());
    }

    @Test
    void shouldNotBalanceEquationWithEmptyParenthesesInProducts() {
        EquationResult result = equationService.tryBalance("KClO3", "K()Cl + O2", "sp");
        assertFalse(result.isPresent());
    }

    @Test
    void shouldNotBalanceEquationWithUnbalancedParenthesesInReactants() {
        EquationResult result = equationService.tryBalance("KCl((O3)2", "KCl + 3O2", "sp");
        assertFalse(result.isPresent());
    }

    @Test
    void shouldNotBalanceEquationWithUnbalancedParenthesesInProducts() {
        EquationResult result = equationService.tryBalance("KClO3", "K(Cl))3 + O2", "sp");
        assertFalse(result.isPresent());
    }

    @Test
    void shouldNotBalanceEquationWithMismatchedElements() {
        EquationResult result = equationService.tryBalance("H2 + O2 + Cl", "H2O", "sp");
        assertFalse(result.isPresent());
    }

    @Test
    void shouldNotBalanceUnbalanceableEquation() {
        EquationResult result = equationService.tryBalance("CO3", "CO3 + O2", "sp");
        assertFalse(result.isPresent());
    }

}