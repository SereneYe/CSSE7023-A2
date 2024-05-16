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

    public void assertToken(ComplexScanner.Token token, ComplexScanner.TokenType expectedType, String expectedName, String expectedContent){
        assertEquals(expectedType, token.type());
        assertEquals(expectedName, token.name());
        assertEquals(expectedContent, token.contents());
    }
    ///////////////////////////////////////////////////
    // Tokenize Tests
    ///////////////////////////////////////////////////
    @Test
    public void testTokenizeNormal1() throws ParseException {
        String input = "3 * (4 + 2)";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(3, tokens.size());
        assertToken(tokens.get(0), ComplexScanner.TokenType.CONST, "3", null);
        assertToken(tokens.get(1), ComplexScanner.TokenType.OP, "*", null);
        assertToken(tokens.get(2), ComplexScanner.TokenType.FUNC, "", "4+2");
    }

    @Test
    public void testTokenizeNormal2() throws ParseException {
        String input = "3 * 4 + 2";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(5, tokens.size());
        assertToken(tokens.get(0), ComplexScanner.TokenType.CONST, "3", null);
        assertToken(tokens.get(1), ComplexScanner.TokenType.OP, "*", null);
        assertToken(tokens.get(2), ComplexScanner.TokenType.CONST, "4", null);
        assertToken(tokens.get(3), ComplexScanner.TokenType.OP, "+", null);
        assertToken(tokens.get(4), ComplexScanner.TokenType.CONST, "2", null);
    }

    @Test
    public void testTokenizeNormal3() throws ParseException {
        String input = "3 / 4 / 0";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(5, tokens.size());

        assertToken(tokens.get(0), ComplexScanner.TokenType.CONST, "3", null);
        assertToken(tokens.get(1), ComplexScanner.TokenType.OP, "/", null);
        assertToken(tokens.get(2), ComplexScanner.TokenType.CONST, "4", null);
        assertToken(tokens.get(3), ComplexScanner.TokenType.OP, "/", null);
        assertToken(tokens.get(4), ComplexScanner.TokenType.CONST, "0", null);
    }

    @Test
    public void testTokenizeNormal4() throws ParseException {
        String input = "3 ,   4 ,   0,";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(6, tokens.size());

        assertToken(tokens.get(0), ComplexScanner.TokenType.CONST, "3", null);
        assertToken(tokens.get(1), ComplexScanner.TokenType.OP, ",", null);
        assertToken(tokens.get(2), ComplexScanner.TokenType.CONST, "4", null);
        assertToken(tokens.get(3), ComplexScanner.TokenType.OP, ",", null);
        assertToken(tokens.get(4), ComplexScanner.TokenType.CONST, "0", null);
        assertToken(tokens.get(5), ComplexScanner.TokenType.OP, ",", null);
    }

    @Test
    public void testTokenizeOperator() throws ParseException {
        String input = "+";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(1, tokens.size());
        assertToken(tokens.get(0), ComplexScanner.TokenType.OP, "+", null);
    }

    @Test
    public void testTokenizeConstant() throws ParseException {
        String input = "    42    ";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(1, tokens.size());
        assertToken(tokens.get(0), ComplexScanner.TokenType.CONST, "42", null);
    }

    @Test
    public void testTokenizeReference() throws ParseException {
        String input = "A2";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(1, tokens.size());
        assertToken(tokens.get(0),ComplexScanner.TokenType.REFERENCE,"A2",null);

    }

    @Test
    public void testTokenizeFunction() throws ParseException {
        String input = "MEAN(2 , 3)";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(1, tokens.size());
        assertToken(tokens.get(0), ComplexScanner.TokenType.FUNC, "MEAN", "2,3");
    }

    @Test(expected = ParseException.class)
    public void testTokenizeUnmatchedOpenParenthesis() throws ParseException {
        String input = "(3 * (4 + 2";
        ComplexScanner.tokenize(input);
        fail("Should have thrown ParseException");
    }

    @Test(expected = ParseException.class)
    public void testTokenizeUnmatchedParenthesis() throws ParseException {
        String input = "(3 * (4 + 2)";
        ComplexScanner.tokenize(input);
        fail("Should have thrown ParseException");
    }

    @Test(expected = ParseException.class)
    public void testTokenizeUnmatchedCloseParenthesis() throws ParseException {
        String input = "3 * (4 + 2))";
        ComplexScanner.tokenize(input);
    }

    @Test
    public void testTokenizeEmptyString() throws ParseException {
        String input = "";
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

        assertToken(tokens.get(0), ComplexScanner.TokenType.FUNC, "", "5");
        assertToken(tokens.get(1), ComplexScanner.TokenType.OP, "+", null);
        assertToken(tokens.get(2), ComplexScanner.TokenType.FUNC, "MEAN", "a,b");
    }

    @Test
    public void testTokenizeMoreComplexExpression() throws ParseException {
        String input = "fun1(fun2(10 - A3) / 4)";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(1, tokens.size());
        assertToken(tokens.get(0), ComplexScanner.TokenType.FUNC, "fun1", "fun2(10-A3)/4");
    }

    @Test
    public void testTokenizeUnmatchedOpenParenthesisInFunction() throws ParseException {
        String input = "3,2+5,8";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(7, tokens.size());

        assertToken(tokens.get(0), ComplexScanner.TokenType.CONST, "3", null);
        assertToken(tokens.get(1), ComplexScanner.TokenType.OP, ",", null);
        assertToken(tokens.get(2), ComplexScanner.TokenType.CONST, "2", null);
        assertToken(tokens.get(3), ComplexScanner.TokenType.OP, "+", null);
        assertToken(tokens.get(4), ComplexScanner.TokenType.CONST, "5", null);
        assertToken(tokens.get(5), ComplexScanner.TokenType.OP, ",", null);
        assertToken(tokens.get(6), ComplexScanner.TokenType.CONST, "8", null);
    }

    @Test(expected = ParseException.class)
    public void testTokenizeUnmatchedCloseParenthesisInFunction() throws ParseException {
        String input = ")";
        ComplexScanner.tokenize(input);
    }

    @Test
    public void testTokenizeSpecialCharacter() throws ParseException {
        String input = "&";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(1, tokens.size());
        assertToken(tokens.get(0), ComplexScanner.TokenType.REFERENCE, "&", null);
    }

    @Test
    public void testTokenizeNestedFunctions() throws ParseException {
        String input = "MEAN((2 + 3))";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(1, tokens.size());
        assertToken(tokens.get(0), ComplexScanner.TokenType.FUNC, "MEAN", "(2+3)");
    }

    @Test
    public void testTokenizeFunctionWithNoArguments() throws ParseException {
        String input = "MEDIAN()";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(1, tokens.size());
        assertToken(tokens.get(0), ComplexScanner.TokenType.FUNC, "MEDIAN", "");
    }

    @Test
    public void testTokenizeFunctionWithMultipleArguments() throws ParseException {
        String input = "MEDIAN(2, a, MEDIAN(b))";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(1, tokens.size());

        assertToken(tokens.get(0), ComplexScanner.TokenType.FUNC, "MEDIAN", "2,a,MEDIAN(b)");
    }

    @Test
    public void testTokenizeFunctionWithNestedFunction() throws ParseException {
        String input = "()+";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(2, tokens.size());
        assertToken(tokens.get(0), ComplexScanner.TokenType.FUNC, "", "");
        assertToken(tokens.get(1), ComplexScanner.TokenType.OP, "+", null);
    }

    @Test
    public void testTokenizeFunctionSymbolAsSpecialChar() throws ParseException {
        String input = "+SUM(2, 3)";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(2, tokens.size());
        assertToken(tokens.get(0), ComplexScanner.TokenType.OP, "+", null);
        assertToken(tokens.get(1), ComplexScanner.TokenType.FUNC, "SUM", "2,3");
    }

    @Test
    public void testTokenizeNegativeConstantParseException() throws ParseException {
        String input = "-5";
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        assertEquals(2, tokens.size());
        assertToken(tokens.get(0), ComplexScanner.TokenType.OP, "-", null);
        assertToken(tokens.get(1), ComplexScanner.TokenType.CONST, "5", null);
    }

}