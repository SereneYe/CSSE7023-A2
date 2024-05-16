package sheep.expression.arithmetic;

import org.junit.Test;
import sheep.expression.Expression;
import sheep.expression.TypeError;
import sheep.expression.basic.Constant;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MedianTest {

    @Test
    public void testMedianValueOfOddNumbers() throws TypeError {
        Function median = new Median(new Expression[]{new Constant(2), new Constant(4), new Constant(6)});
        Expression result = median.value(new HashMap<>());
        assertTrue("Result of evaluating median is not a constant.", result instanceof Constant);
        assertEquals("Result of median with odd Constants is incorrect.", 4, ((Constant) result).getValue());
    }

    @Test
    public void testMedianValueOfEvenNumbers() throws TypeError {
        Function median = new Median(new Expression[]{new Constant(2), new Constant(4)});
        Expression result = median.value(new HashMap<>());
        assertTrue("Result of evaluating median is not a constant.", result instanceof Constant);
        assertEquals("Result of median with even Constants is incorrect.", 3, ((Constant) result).getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMedianNoArguments() {
        Function median = new Median(new Expression[0]);
        median.perform(new long[0]);
    }

    @Test
    public void testMedianPerformWithEvenNumbers() {
        Function median = new Median(new Expression[]{new Constant(3), new Constant(3)});
        long result = median.perform(new long[]{3, 3});
        assertEquals("Result of performing median with identic numbers is incorrect.", 3, result);
    }

    @Test
    public void testMedianPerformWithIdenticNumbers() {
        Function median = new Median(new Expression[]{new Constant(3), new Constant(3), new Constant(3),
                new Constant(3)});
        long result = median.perform(new long[]{3, 3, 3, 3});
        assertEquals("Result of performing median with identic numbers is incorrect.", 3, result);
    }

    @Test
    public void testMedianPerformWithRandomSetOfNumbers() {
        Function median = new Median(new Expression[]{new Constant(9), new Constant(20), new Constant(6), new Constant(13), new Constant(7)});
        long result = median.perform(new long[]{9, 20, 6, 13, 7});
        assertEquals("Result of performing median with a random set of numbers is incorrect.", 9, result);
    }

    @Test
    public void testMedianValueWithNegativeNumbers() throws TypeError {
        Function median = new Median(new Expression[]{new Constant(-5), new Constant(-3), new Constant(-10)});
        Expression result = median.value(new HashMap<>());
        assertTrue("Result of evaluating median is not a constant.",
                result instanceof Constant);
        assertEquals("Result of median with negative Constants is incorrect.",
                -5, ((Constant) result).getValue());
    }

    @Test
    public void testMedianPerformWithNegativeNumbers() {
        Function median = new Median(new Expression[]{new Constant(-5), new Constant(-3), new Constant(-10)});
        long result = median.perform(new long[]{-5, -3, -10});
        assertEquals("Result of performing median with negative numbers is incorrect.",-5,result);
    }

    @Test
    public void testMedianPerformWithMixedNegativePositiveNumbers() {
        Function mean = new Median(new Expression[]{new Constant(2), new Constant(-4), new Constant(6)});
        long result = mean.perform(new long[]{2, -4, 6});
        assertEquals("Result of performing median with mixed negative and positive numbers is incorrect.",2,result);
    }

    @Test
    public void testMedianPerformWithRepeatedNumbers() {
        Function mean = new Median(new Expression[]{new Constant(2), new Constant(2), new Constant(2), new Constant(6)});
        long result = mean.perform(new long[]{2, 2, 2, 6});
        assertEquals("Result of performing median with repeated numbers is incorrect.",2,result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMedianPerformWithEmptyArgumentList() {
        Function median = new Median(new Expression[]{});
        median.perform(new long[]{});
    }

    @Test
    public void testMedianPerformWithZero() {
        Function median = new Median(new Expression[]{new Constant(0), new Constant(4)});
        long result = median.perform(new long[]{0, 4});
        assertEquals("Result of median with zero and other number is incorrect.", 2, result);
    }


}