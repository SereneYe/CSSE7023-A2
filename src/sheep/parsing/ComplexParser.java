package sheep.parsing;

import sheep.expression.Expression;

import sheep.expression.ExpressionFactory;
import sheep.expression.InvalidExpression;

import java.util.*;
import java.util.regex.Pattern;

import static sheep.parsing.ComplexScanner.OPERATORS;

/**
 * A parser of Basic, Arithmetic, and broader functional components.
 *
 * @stage1
 */
public class ComplexParser implements Parser {

    /**
     * Variable factory used for constructing expressions.
     * @provided
     */
    private final ExpressionFactory factory;

    /**
     * Regular expression pattern used to split a string on commas outside parentheses.
     * This pattern can be used to split a string on commas outside parentheses.
     */
    private static final Pattern SPLIT_ON_COMMA_OUTSIDE_PARENS =
            Pattern.compile(",(?![^()]*\\))");

    /**
     * Construct a new parser.
     * Parsed expressions are constructed using the expression factory.
     *
     * @param factory Factory used to construct parsed expressions.
     */
    public ComplexParser(ExpressionFactory factory) {
        this.factory = factory;
    }

    /**
     * Attempt to parse a string input into a complex expression.
     *
     * @param input A string to attempt to parse as a complex expression.
     * @return The resulting complex expression.
     * @throws ParseException If the input string cannot be parsed correctly.
     * @throws InvalidExpression If there is an error during parsing.
     */
    public Expression tryParseComplex(String input) throws ParseException, InvalidExpression {
        List<ComplexScanner.Token> tokens = ComplexScanner.tokenize(input);
        return parseWithPrecedence(tokens, 0);
    }

    /**
     * Parse a list of tokens into an expression, considering operator precedence.
     *
     * @param tokens The list of tokens to parse.
     * @param cur_precedence The current operator precedence level.
     * @return The parsed expression.
     * @throws ParseException If there is an error during parsing.
     * @throws InvalidExpression If the expression is invalid.
     */
    public Expression parseWithPrecedence(List<ComplexScanner.Token> tokens, int cur_precedence)
            throws ParseException, InvalidExpression {

        if (tokens.isEmpty()) {
            return factory.createEmpty();
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
        } else if (tokens.get(0).name().contains("-")) {
            return factory.createOperator("-",
                    new Expression[]{factory.createEmpty(), parseWithPrecedence(
                            tokens.subList(1, tokens.size()), 0)});

        } else if (tokens.size() % 2 == 1) {
            char operator = OPERATORS.get(cur_precedence);
            List<Integer> operatorIndices = new ArrayList<>();

            for (int i = 1; i < tokens.size(); i += 2) {
                ComplexScanner.Token token = tokens.get(i);
                if (token.type().equals(ComplexScanner.TokenType.OP) &&
                        token.name().equals(String.valueOf(operator))) {
                    operatorIndices.add(i);
                }
            }

            if (!operatorIndices.isEmpty()) {
                List<Object> parsedTokens = new ArrayList<>();
                for (int index = 0; index < operatorIndices.size(); index++) {
                    int startIndex = index == 0 ? 0 : operatorIndices.get(index - 1) + 1;
                    int endIndex = operatorIndices.get(index);
                    List<ComplexScanner.Token> subTokens = tokens.subList(startIndex, endIndex);
                    parsedTokens.add(parseWithPrecedence(
                            new ArrayList<>(subTokens), 0));
                }
                parsedTokens.add(parseWithPrecedence(new ArrayList<>(
                        tokens.subList(operatorIndices.get(operatorIndices.size() - 1) + 1,
                                tokens.size())), 0));
                return factory.createOperator(String.valueOf(operator), parsedTokens.toArray());
            }

            if (cur_precedence < OPERATORS.size() - 1) {
                return parseWithPrecedence(tokens, cur_precedence + 1);
            }
        } else {
            throw new ParseException("Invalid expression: Inconsistency between operators and " +
                    "numbers of tokens");
        }
        throw new ParseException("Invalid expression: " + tokens);
    }


    /**
     * Attempt to parse a string expression into an expression.
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