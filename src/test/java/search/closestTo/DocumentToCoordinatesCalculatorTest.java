package search.closestTo;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static search.closestTo.DocumentToCoordinatesCalculator.getCoordinateOfString;

public class DocumentToCoordinatesCalculatorTest {

    @Test
    public void getCoordinateOfStringTest() {
        String testData0 = "qwertyuiop";
        String testData1 = "qwertyuiop12";

        assertEquals(getCoordinateOfString(testData0), getCoordinateOfString(testData0));
        assertEquals(getCoordinateOfString(testData1), getCoordinateOfString(testData1));
        assertNotEquals(getCoordinateOfString(testData0), getCoordinateOfString(testData1));
    }

    @Test
    public void getCoordinateOfStringDistanceTest() {
        String testData0 = "qwerty34";
        int testDataCoordinate0 = getCoordinateOfString(testData0);
        String testData1 = "qwerty12";
        int testDataCoordinate1 = getCoordinateOfString(testData1);
        String testData2 = "0werty12";
        int testDataCoordinate2 = getCoordinateOfString(testData2);

        assertNotEquals(testDataCoordinate0, testDataCoordinate1);
        assertNotEquals(testDataCoordinate0, testDataCoordinate2);
        assertNotEquals(testDataCoordinate1, testDataCoordinate2);

        //checking that strings with same prefix are closer then strings with different (working for strings of same size)
        Assert.assertTrue(Math.abs(testDataCoordinate0 - testDataCoordinate1) < Math.abs(testDataCoordinate0 - testDataCoordinate2));
    }

}