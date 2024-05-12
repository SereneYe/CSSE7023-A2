package sheep.parsing;

import sheep.expression.Expression;
import sheep.expression.ExpressionFactory;
import sheep.expression.InvalidExpression;

import java.util.ArrayList;
import java.util.List;

import static sheep.parsing.ComplexScanner.OPERATORS;

/**
 * A parser of Basic, Arithmetic, and broader functional components.
 *
 * @stage1
 */
public class ComplexParser3 implements Parser {

    private final ExpressionFactory factory;

    /**
     * Construct a new parser.
     * Parsed expressions are constructed using the expression factory.
     *
     * @param factory Factory used to construct parsed expressions.
     */
    public ComplexParser3(ExpressionFactory factory) {
        this.factory = factory;
    }

    private Object[] tryParseComplex(String[] inputs) throws ParseException, InvalidExpression {
        Object[] expressions = new Object[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            expressions[i] = tryParseComplex(inputs[i]);
        }
        return expressions;
    }

//    private Expression tryParseFunction()

    public Object tryParseComplex(String input) throws ParseException, InvalidExpression {
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        return parseWithPrecedence(tokens, 0);
    }

    public Object parseWithPrecedence(List<ComplexScanner.Token> tokens, int cur_precedence) throws ParseException, InvalidExpression {
        if (tokens.size() == 1) {
            ComplexScanner.Token token = tokens.get(0);
            if (token.type().equals(ComplexScanner.TokenType.FUNC)) {
                return factory.createOperator(token.name(), new Object[]{tryParseComplex(token.contents())});
            }
            if (token.type().equals(ComplexScanner.TokenType.CONST)) {
                return factory.createConstant(Long.parseLong(token.name()));
            }
            if (token.type().equals(ComplexScanner.TokenType.REFERENCE)) {
                return factory.createReference(token.name());
            }
        } else if (tokens.size() % 2 == 1) {
            char operator = OPERATORS.get(cur_precedence);
            for (int i = 1; i < tokens.size(); i += 2) {
                ComplexScanner.Token token = tokens.get(i);
                if (token.type().equals(ComplexScanner.TokenType.OP) && token.name().equals(String.valueOf(operator))) {
                    List<ComplexScanner.Token> left = tokens.subList(0, i);
                    List<ComplexScanner.Token> right = tokens.subList(i + 1, tokens.size());
                    Object leftObj = parseWithPrecedence(new ArrayList<>(left), 0);
                    Object rightObj = parseWithPrecedence(new ArrayList<>(right), 0);
                    return factory.createOperator(token.name(), new Object[]{leftObj, rightObj});
                }
            }
            if (cur_precedence < OPERATORS.size() - 1) {
                return parseWithPrecedence(tokens, cur_precedence + 1);
            }
        }
        return null;
    }


    /**
     * Attempt to parse a string expression into an expression.
     *
     * <p>
     * If the string contains parentheses "()" than the contents of the parentheses should be parsed.
     * The contents between should follow the same rules as the top-level expression.
     * e.g. leading and trailing whitespace should be ignored, etc.
     * The parenthesised expressions should be constructed using ExpressionFactory::createOperator().
     * If the parentheses are preceded by a name  the operator should have that name,
     * Otherwise it should have the name "".
     * If the contents of the parentheses is an ExpressionList the list's expressions should be extracted
     * and passed directly to createOperator().
     * </p>
     * <p>
     * Any string that contains commas "," should be split on "," and the components between should be parsed.
     * The components between should follow the same rules as the top-level expression,
     * If any component cannot be parsed, the whole expression cannot be parsed.
     * The comma delimited expressions should be constructed using ExpressionFactory::createOperator(",", expressions)
     * </p>
     * <p>
     * Any remaining String should be parsed identically to SimpleParser
     * You may wish to use {@link ComplexScanner#tokenize(String)} to help parse these elements.
     *
     * <pre>
     * {@code
     * ExpressionFactory factory = new CoreFactory();
     * Parser parser = new ComplexParser(factory);
     * parser.parse("  42  "); // Constant(42)
     * parser.parse("HEY "); // Reference("HEY")
     * parser.parse("hello + world"); // Plus(Reference("hello"), Reference("world"))
     * parser.parse("4 + 5 + 7 * 12 + 3"); // Plus(Constant(4), Constant(5), Times(Constant(7), Constant(12)), Constant(3))
     * parser.parse("(2+3)-(2+3)"); // Minus(Plus(Constant(2), Constant(3)), Plus(Constant(2), Constant(3)))
     * parser.parse("(hello)"); // Identity(Reference("hello"))
     * parser.parse("(() + ())"); // Identity(Plus(Identity(Nothing()), Identity(Nothing())))
     * }</pre>
     *
     * @param input A string to attempt to parse.
     * @return The result of parsing the expression.
     * @throws ParseException If the string input is not recognisable as an expression.
     * @hint Use {@link ComplexScanner} to tokenize the input string to make it easier to parse.
     */
    @Override
    public Expression parse(String input) throws ParseException {
        try {
            return (Expression) tryParseComplex(input);
        } catch (InvalidExpression e) {
            throw new ParseException(e);
        }
    }
}
