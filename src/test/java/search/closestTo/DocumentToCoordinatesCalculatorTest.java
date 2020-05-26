package search.closestTo;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static search.closestTo.DocumentToCoordinatesCalculator.stringToCoordinate;

public class DocumentToCoordinatesCalculatorTest {

    @Test
    public void getCoordinateOfStringTest() {
        String testData0 = "qwertyuiop";
        String testData1 = "qwertyuiop12";

        assertEquals(stringToCoordinate(testData0), stringToCoordinate(testData0));
        assertEquals(stringToCoordinate(testData1), stringToCoordinate(testData1));
        assertNotEquals(stringToCoordinate(testData0), stringToCoordinate(testData1));
    }

    @Test
    public void getCoordinateOfStringDistanceTest() {
        String testData0 = "qwerty34";
        int testDataCoordinate0 = stringToCoordinate(testData0);
        String testData1 = "qwerty12";
        int testDataCoordinate1 = stringToCoordinate(testData1);
        String testData2 = "0werty12";
        int testDataCoordinate2 = stringToCoordinate(testData2);

        assertNotEquals(testDataCoordinate0, testDataCoordinate1);
        assertNotEquals(testDataCoordinate0, testDataCoordinate2);
        assertNotEquals(testDataCoordinate1, testDataCoordinate2);

        //checking that strings with same prefix are closer then strings with different (working for strings of same size)
        Assert.assertTrue(Math.abs(testDataCoordinate0 - testDataCoordinate1) < Math.abs(testDataCoordinate0 - testDataCoordinate2));
    }

}