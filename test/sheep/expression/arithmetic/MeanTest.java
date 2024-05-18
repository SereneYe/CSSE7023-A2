package sheep.expression.arithmetic;

import org.junit.Test;
import sheep.expression.Expression;
import sheep.expression.TypeError;
import sheep.expression.basic.Constant;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MeanTest {

    @Test
    public void testMeanValueOfPositiveNumbers() throws TypeError {
        Function mean = new Mean(new Expression[]{new Constant(2), new Constant(4), new Constant(6)});
        Expression result = mean.value(new HashMap<>());
        assertTrue("Result of evaluating mean is not a constant.",
                result instanceof Constant);
        assertEquals("Result of mean with positive Constants is incorrect.",
                4, ((Constant) result).getValue());
    }

    @Test
    public void testMoreNumbersMeanValue() throws TypeError {
        Function mean = new Mean(new Expression[]{new Constant(2), new Constant(3), new Constant(5), new Constant(8)});
        Expression result = mean.value(new HashMap<>());
        assertTrue("Result of evaluating mean is not a constant.",
                result instanceof Constant);
        assertEquals("Result of mean with more Constants is incorrect.",
                4, ((Constant) result).getValue());
    }

    @Test
    public void testMeanValueWithNegativeNumbers() throws TypeError {
        Function mean = new Mean(new Expression[]{new Constant(-4), new Constant(-6), new Constant(-8)});
        Expression result = mean.value(new HashMap<>());
        assertTrue("Result of evaluating mean is not a constant.",
                result instanceof Constant);
        assertEquals("Result of mean with negative Constants is incorrect.",
                -6, ((Constant) result).getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMeanNoArguments() {
        Function mean = new Mean(new Expression[0]);
        long result =mean.perform(new long[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMeanInvalidArguments1() {
        Function mean = new Mean(new Expression[]{new Identity(new Expression[0])});
        long result =mean.perform(new long[0]);
    }

    @Test
    public void testMeanArguments2() {
        Function mean = new Mean(new Expression[]{new Identity(new Expression[]{new Constant(1)}),
                new Identity(new Expression[]{new Plus(new Expression[]{new Constant(1)})}),
                new Identity(new Expression[]{new Constant(5)})});
        long result = mean.perform(new long[]{1,2,5});
        assertEquals("Result of performing mean with negative numbers is incorrect.",2,result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMeanInvalidArguments3() {
        Function mean = new Mean(new Expression[]{});
        long result =mean.perform(new long[0]);
    }

    @Test
    public void testMeanPerformWithNegativeNumbers() {
        Function mean = new Mean(new Expression[]{new Constant(-4), new Constant(-6), new Constant(-8)});
        long result = mean.perform(new long[]{-4, -6, -8});
        assertEquals("Result of performing mean with negative numbers is incorrect.",-6,result);
    }

    @Test
    public void testMeanPerformWithPositiveNumbers() {
        Function mean = new Mean(new Expression[]{new Constant(1), new Constant(4),
                new Constant(6)});
        long result = mean.perform(new long[]{1, 4, 6});
        assertEquals("Result of performing mean with positive numbers is incorrect.",3,result);
    }

    @Test
    public void testMeanPerformWithZeroNumbers() {
        Function mean = new Mean(new Expression[]{new Constant(0), new Constant(0),
                new Constant(-1)});
        long result = mean.perform(new long[]{0, 0, -1});
        assertEquals("Result of performing mean with zero numbers is incorrect.",0,result);
    }


    @Test
    public void testMeanOfPositiveNumbers() throws TypeError {
        Function mean = new Mean(new Expression[]{new Constant(2), new Constant(4), new Constant(6)});
        long result = mean.perform(new long[]{2, 5, 6});
        assertEquals("Result of calculating mean of positive numbers is incorrect.",
                4, result);
    }

    @Test
    public void testMeanOfNegativeNumbers() throws TypeError {
        Function mean = new Mean(new Expression[]{new Constant(-2), new Constant(-4), new Constant(-6)});
        long result = mean.perform(new long[]{-2, -5, -6});
        assertEquals("Result of calculating mean of negative numbers is incorrect.",
                -4, result);
    }

    @Test
    public void testMeanOfMixedSignNumbers() throws TypeError {
        Function mean = new Mean(new Expression[]{new Constant(-2), new Constant(7)});
        long result = mean.perform(new long[]{-2, 7});
        assertEquals("Result of calculating mean of mixed sign numbers is incorrect.",
                2, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMeanOfNoNumbers() throws TypeError {
        Function mean = new Mean(new Expression[]{});
        long result = mean.perform(new long[]{});
    }
}