package search.request;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConverterUtilsTest {

    @Test
    public void checkIfParenthesisBalancedTest() {
        assertTrue(ConverterUtils.checkIfParenthesisBalanced("()"));
        assertTrue(ConverterUtils.checkIfParenthesisBalanced("(())"));
        assertTrue(ConverterUtils.checkIfParenthesisBalanced("(()())"));
        assertFalse(ConverterUtils.checkIfParenthesisBalanced("("));
        assertFalse(ConverterUtils.checkIfParenthesisBalanced("(("));
        assertFalse(ConverterUtils.checkIfParenthesisBalanced("(()"));
        assertFalse(ConverterUtils.checkIfParenthesisBalanced("))"));
        assertFalse(ConverterUtils.checkIfParenthesisBalanced(")("));
    }

}