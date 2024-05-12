package sheep.parsing;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ComplexScannerTest {
    @Before
    public void setup() {
        // Setup any common test data
    }

    ///////////////////////////////////////////////////
    // Tokenize Tests
    ///////////////////////////////////////////////////
    @Test
    public void testTokenize() throws ParseException {
        String input = "3 * (4 + 2)";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(3, tokens.size());

        assertEquals(ComplexScanner.TokenType.CONST, tokens.get(0).type());
        assertEquals("3", tokens.get(0).name());

        assertEquals(ComplexScanner.TokenType.OP, tokens.get(1).type());
        assertEquals("*", tokens.get(1).name());

        assertEquals(ComplexScanner.TokenType.FUNC, tokens.get(2).type());
        assertEquals("", tokens.get(2).name());
        assertEquals("4+2", tokens.get(2).contents());
    }

    @Test(expected = ParseException.class)
    public void testTokenizeUnmatchedParenthesis() throws ParseException {
        String input = "(3 * (4 + 2))";
        ComplexScanner.tokenize(input);
        fail("Should have thrown ParseException");
    }

    @Test
    public void testTokenizeOperator() throws ParseException {
        String input = "+";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(1, tokens.size());
        assertEquals(ComplexScanner.TokenType.OP, tokens.get(0).type());
        assertEquals("+", tokens.get(0).name());
    }

    @Test
    public void testTokenizeConstant() throws ParseException {
        String input = "42";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(1, tokens.size());
        assertEquals(ComplexScanner.TokenType.CONST, tokens.get(0).type());
        assertEquals("42", tokens.get(0).name());
    }

    @Test
    public void testTokenizeReference() throws ParseException {
        String input = "A2";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(1, tokens.size());
        assertEquals(ComplexScanner.TokenType.REFERENCE, tokens.get(0).type());
        assertEquals("A2", tokens.get(0).name());
    }

    @Test
    public void testTokenizeFunction() throws ParseException {
        String input = "MEAN(2 , 3)";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(1, tokens.size());
        assertEquals(ComplexScanner.TokenType.FUNC, tokens.get(0).type());
        assertEquals("MEAN", tokens.get(0).name());
        assertEquals("2,3", tokens.get(0).contents());
    }

    @Test(expected = ParseException.class)
    public void testTokenizeUnmatchedOpenParenthesis() throws ParseException {
        String input = "(3 * (4 + 2";
        ComplexScanner.tokenize(input);
    }

    @Test(expected = ParseException.class)
    public void testTokenizeUnmatchedCloseParenthesis() throws ParseException {
        String input = "3 * (4 + 2))";
        ComplexScanner.tokenize(input);
    }

    @Test
    public void testTokenizeEmptyString() throws ParseException {
        String input = " ";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(0, tokens.size());
    }

    @Test
    public void testTokenizeWhitespaceString() throws ParseException {
        String input = "     ";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(0, tokens.size());
    }

    @Test
    public void testTokenizeComplexExpression() throws ParseException {
        String input = "(5) + MEAN(a, b)";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(3, tokens.size());

        assertEquals(ComplexScanner.TokenType.FUNC, tokens.get(0).type());
        assertEquals("", tokens.get(0).name());
        assertEquals("5", tokens.get(0).contents());

        assertEquals(ComplexScanner.TokenType.OP, tokens.get(1).type());
        assertEquals("+", tokens.get(1).name());

        assertEquals(ComplexScanner.TokenType.FUNC, tokens.get(2).type());
        assertEquals("MEAN", tokens.get(2).name());
        assertEquals("a,b", tokens.get(2).contents());

    }

    @Test
    public void testTokenizeMoreComplexExpression() throws ParseException {
        String input = "fun1(fun2(10 - A3) / 4) * MEAN(5, A2, C2) + 3";
//        String input = "5, A2, C2";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(5, tokens.size());

    }

    @Test
    public void testTokenizeUnmatchedOpenParenthesisInFunction() throws ParseException {
//        String input = "MEAN((2 + 3), 4)";
        String input = "3,2+5,8";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        System.out.println(tokens);
    }

    @Test(expected = ParseException.class)
    public void testTokenizeUnmatchedCloseParenthesisInFunction() throws ParseException {
        String input = "MEAN(2 + 3))";
        ComplexScanner.tokenize(input);
    }

    @Test
    public void testTokenizeNestedFunctions() throws ParseException {
        String input = "MEAN((2 + 3))";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(ComplexScanner.TokenType.FUNC, tokens.get(0).type());
        assertEquals("MEAN", tokens.get(0).name());
        assertEquals("(2+3)", tokens.get(0).contents());
    }

    @Test
    public void testTokenizeFunctionWithNoArguments() throws ParseException {
        String input = "MEDIAN()";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(1, tokens.size());

        assertEquals(ComplexScanner.TokenType.FUNC, tokens.get(0).type());
        assertEquals("MEDIAN", tokens.get(0).name());
        assertEquals("", tokens.get(0).contents());
    }

    @Test
    public void testTokenizeFunctionWithMultipleArguments() throws ParseException {
        String input = "MEDIAN(2, a, MEDIAN(b))";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(1, tokens.size());

        assertEquals(ComplexScanner.TokenType.FUNC, tokens.get(0).type());
        assertEquals("MEDIAN", tokens.get(0).name());
        assertEquals("2,a,MEDIAN(b)", tokens.get(0).contents());
    }

    @Test
    public void testTokenizeFunctionWithNestedFunction() throws ParseException {
        String input = "func1(func2(2 + 3))";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(1, tokens.size());

        assertEquals(ComplexScanner.TokenType.FUNC, tokens.get(0).type());
        assertEquals("func1", tokens.get(0).name());
        assertEquals("func2(2+3)", tokens.get(0).contents());
    }
}