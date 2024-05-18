package sheep.expression.arithmetic;

import org.junit.Test;
import sheep.expression.Expression;
import sheep.expression.TypeError;
import sheep.expression.basic.Constant;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static sheep.core.UpdateResponse.fail;

public class MedianTest {


    @Test
    public void testIdentityPerform() throws TypeError {
        Function arith = new Median(new Expression[]{new Constant(20)});
        long result = arith.perform(new long[]{20});
        assertEquals("Result of performing minus with a single Constant is incorrect.", 20, result);
    }

    @Test
    public void testTwoValue() throws TypeError {
        Function arith = new Median(new Expression[]{new Constant(20), new Constant(11)});
        Expression result = arith.value(new HashMap<>());
        assertTrue("Result of evaluating minus is not a constant.",
                result instanceof Constant);
        assertEquals("Result of minus with two Constants is incorrect.",
                15, ((Constant) result).getValue());
    }


    @Test
    public void testNValue() throws TypeError {
        Function arith = new Median(
                new Expression[]{new Constant(20), new Constant(2), new Constant(2), new Constant(2)}
        );
        Expression result = arith.value(new HashMap<>());
        assertTrue("Result of evaluating minus is not a constant.",
                result instanceof Constant);
        assertEquals("Result of minus with multiple Constants is incorrect.",
                2, ((Constant) result).getValue());
    }

    @Test
    public void testTriPerform() throws TypeError {
        Function arith = new Median(
                new Expression[]{new Constant(20), new Constant(10), new Constant(11),
                        new Constant(1),
                       }
        );
        long result = arith.perform(new long[]{20, 10, 11});
        assertEquals("Result of performing minus with multiple Constants is incorrect",
                11, result);
    }

    @Test
    public void testNPerform() throws TypeError {
        Function arith = new Median(
                new Expression[]{new Constant(20), new Constant(10), new Constant(2),
                        new Constant(2)}
        );
        long result = arith.perform(new long[]{20, 10, 2, 2});
        assertEquals("Result of performing minus with multiple Constants is incorrect",6, result);
    }


    @Test
    public void testSingleNegativeNumber() throws TypeError {
        Function arith = new Median(new Expression[]{new Constant(-20)});
        long result = arith.perform(new long[]{-20});
        assertEquals("Result of Minus with a single negative number is incorrect.",
                -20, result);
    }

    @Test
    public void testMultipleNegativeNumbers() throws TypeError {
        Function arith = new Median(new Expression[]{new Constant(-10), new Constant(-5),
                new Constant(-6)});
        long result = arith.perform(new long[]{-10,-5,-6});
        assertEquals("Result of Minus with a single negative number is incorrect.",
                -6, result);
    }

    @Test
    public void testTriNegativeNumbers() throws TypeError {
        Function arith = new Median(new Expression[]{new Constant(-10), new Constant(-5)});
        long result = arith.perform(new long[]{-10, -5});
        assertEquals("Result of Minus with a single negative number is incorrect.",
                -7, result);
    }

    @Test
    public void testMultipleIdenticalNumbers() throws TypeError {
        Function arith = new Median(new Expression[]{new Constant(5), new Constant(5), new Constant(5)});
        long result = arith.perform(new long[]{5,5,5});
        assertEquals("Result of Minus with a single negative number is incorrect.",
                5, result);
    }

    @Test
    public void testMultipleIdenticalNegativeNumbers() throws TypeError {
        Function arith = new Median(new Expression[]{new Constant(-5), new Constant(-5),
                new Constant(-5)});
        long result = arith.perform(new long[]{-5,-5,-5});
        assertEquals("Result of Minus with a single negative number is incorrect.",
                -5, result);
    }

    @Test(expected = Exception.class) // Update Exception.class to your specific exception: e.g, InvalidArgumentException.class
    public void testEmptyInput() throws TypeError {
        Function arith = new Median(new Expression[]{});
        long result = arith.perform(new long[]{});
        fail("Expected an exception to be thrown");
    }

    @Test // Update Exception.class to your specific exception: e.g, InvalidArgumentException.class
    public void testOperatorInput() throws TypeError {
        Function arith = new Median(new Expression[]{new Plus(new Expression[]{new Constant(1)})});
        long result = arith.perform(new long[]{1});
        assertEquals("Result of Minus with a single negative number is incorrect.",
                1, result);
    }

}
