package sheep.parsing;

import sheep.expression.Expression;

import sheep.expression.ExpressionFactory;
import sheep.expression.InvalidExpression;

import java.util.*;
import java.util.regex.Pattern;

/**
 * A parser of Basic, Arithmetic, and broader functional components.
 *
 * @stage1
 */
public class ComplexParser implements Parser {

    private final ExpressionFactory factory;
    private static final Pattern SPLIT_ON_COMMA_OUTSIDE_PARENS = Pattern.compile(",(?![^()]*\\))");

    /**
     * Construct a new parser.
     * Parsed expressions are constructed using the expression factory.
     *
     * @param factory Factory used to construct parsed expressions.
     */
    public ComplexParser(ExpressionFactory factory) {
        this.factory = factory;
    }


    private List<List<ComplexScanner.Token>> splitTokensByOperator(List<ComplexScanner.Token> tokens, String operator) {
        List<List<ComplexScanner.Token>> splitTokens = new ArrayList<>();
        List<ComplexScanner.Token> currentSubTokens = new ArrayList<>();

        for (ComplexScanner.Token token : tokens) {
            if (token.type().equals(ComplexScanner.TokenType.OP) && token.name().equals(operator)) {
                splitTokens.add(currentSubTokens);
                currentSubTokens = new ArrayList<>();
            } else {
                currentSubTokens.add(token);
            }
        }

        // add last sublist
        splitTokens.add(currentSubTokens);

        return splitTokens;
    }

    private Expression tryParseComplex(List<ComplexScanner.Token> tokens) throws ParseException,
            InvalidExpression {
        if ( tokens.get(0).name().equals("-")) {
            return factory.createOperator("-",
                    new Expression[] {factory.createEmpty(),tryParseComplex(tokens.subList(1,
                    tokens.size()))});
        }
        if (tokens.size() == 1) {
            ComplexScanner.Token token = tokens.get(0);
            if (token.type().equals(ComplexScanner.TokenType.FUNC)) {
                String[] contents = SPLIT_ON_COMMA_OUTSIDE_PARENS.split(token.contents());
                Object[] parsedContents = new Object[contents.length];
                for (int i = 0; i < contents.length; i++) {
                    parsedContents[i] = tryParseComplex(contents[i]);
                }
                return factory.createOperator(token.name(), parsedContents);
            }
            if (token.type().equals(ComplexScanner.TokenType.CONST)) {
                return factory.createConstant(Long.parseLong(token.name()));
            }
            if (token.type().equals(ComplexScanner.TokenType.REFERENCE)) {
                return factory.createReference(token.name());
            }
        } else {
            String[] operators = {",", "=", "<", "-", "+", "*", "/"};
            for (String operator : operators) {
                for (int i = 1; i < tokens.size(); i += 2) {
                    ComplexScanner.Token token = tokens.get(i);
                    if (Objects.equals(token.type(), ComplexScanner.TokenType.OP)) {
                        if (token.name().contains(operator)) {
                            List<List<ComplexScanner.Token>> splitTokens = splitTokensByOperator(tokens, operator);
                            Expression[] expressions = new Expression[splitTokens.size()];
                            for (int j = 0; j < splitTokens.size(); j++) {
                                expressions[j] = tryParseComplex(splitTokens.get(j));
                            }
                            return factory.createOperator(operator, expressions);
                        }
                    }
                }
            }
        }
        return factory.createEmpty();

    }

    public Expression tryParseComplex(String input) throws ParseException, InvalidExpression {

        input = input.strip();
        if (input.isEmpty()) {
            return factory.createEmpty();
        }
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        return tryParseComplex(tokens);
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
            return tryParseComplex(input);
        } catch (InvalidExpression e) {
            throw new ParseException(e);
        }
    }
}