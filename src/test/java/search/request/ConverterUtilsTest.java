package search.request;

import org.junit.Test;
import search.ConditionType;

import static org.junit.Assert.*;

public class ConverterUtilsTest {

    @Test
    public void stripSearchRequestStringTest() {
        assertEquals("(attribute,EQ,value)", ConverterUtils.stripSearchRequestString("(attribute,EQ,value)"));
        assertEquals("(attribute,EQ,value)", ConverterUtils.stripSearchRequestString("(((attribute,EQ,value)))"));
        assertEquals("(attribute1,EQ,value1)or(attribute2,LT,value2)",
                ConverterUtils.stripSearchRequestString("(((attribute1,EQ,value1)or(attribute2,LT,value2)))"));
    }

    @Test
    public void checkIfParenthesisBalancedTest() {
        assertTrue(ConverterUtils.checkIfParenthesisBalanced(""));
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