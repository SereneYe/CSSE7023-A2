package sheep.parsing;

import sheep.expression.Expression;
import sheep.expression.ExpressionFactory;
import sheep.expression.InvalidExpression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A parser of Basic, Arithmetic, and broader functional components.
 * @stage1
 */
public class ComplexParser2 implements Parser {

    private final ExpressionFactory factory;

    /**
     * Construct a new parser.
     * Parsed expressions are constructed using the expression factory.
     *
     * @param factory Factory used to construct parsed expressions.
     */
    public ComplexParser2(ExpressionFactory factory){
        this.factory = factory;
    }




    private List<Object> tryParseComplex(List<ComplexScanner.Token> tokens) throws ParseException, InvalidExpression {
        System.out.println(tokens);
        List<Object> results = new ArrayList<>();

        for (ComplexScanner.Token token : tokens) {
            if (token.type().equals(ComplexScanner.TokenType.OP)) {

            } else if (token.type().equals(ComplexScanner.TokenType.REFERENCE)) {
                // 解析引用Token的逻辑
                results.add(factory.createReference(token.name()));
            } else if (token.type().equals(ComplexScanner.TokenType.CONST)) {
                // 解析常量Token的逻辑
                results.add(factory.createConstant(Long.parseLong(token.name())));
            } else if (token.type().equals(ComplexScanner.TokenType.FUNC)) {
                // 解析函数Token的逻辑
                String contents = token.contents();
                List<ComplexScanner.Token> splitTokens;
                try {
                    splitTokens = ComplexScanner.tokenize(contents);
                } catch (ParseException e) {
                    //处理ComplexScanner可能抛出的异常
                    throw new ParseException("Failed to parse input: " + contents);
                }

                List<Object> tokenList = new ArrayList<>();
                for (ComplexScanner.Token splitToken : splitTokens) {
                    List<Object> parsedTokens = tryParseComplex(Collections.singletonList(splitToken));
                    if (!parsedTokens.isEmpty()) {
                        tokenList.addAll(parsedTokens);
                    }
                }
                //将List转换为数组
                Object[] tokenArray = tokenList.toArray();
                results.add(factory.createOperator(token.name(), tokenArray));
            }
        }
        return results;
    }

    private Object tryParseComplex(String input) throws ParseException, InvalidExpression {
        input = input.strip();
        List<ComplexScanner.Token> tokens;
        try {
            tokens = ComplexScanner.tokenize(input);
        } catch (ParseException e) {
            // ignore unable to parse
            throw new ParseException("Failed to parse input: " + input);
        }

        // Process tokens and find operator tokens.
        for (int i = 0; i < tokens.size(); i++) {
            ComplexScanner.Token token = tokens.get(i);
            if (token.type() == ComplexScanner.TokenType.OP) {
                if (i == 0 || i == tokens.size() - 1) {
                    throw new ParseException("Operator cannot be the first or the last token in " +
                            "the expression: " + token.name());
                }
                ComplexScanner.Token previousToken = tokens.get(i - 1);
                ComplexScanner.Token nextToken = tokens.get(i + 1);

                if (previousToken.type() == ComplexScanner.TokenType.OP ||
                        nextToken.type() == ComplexScanner.TokenType.OP) {
                    throw new ParseException("Operator cannot be adjacent to another operator: " +
                            token.name());
                }

                List<ComplexScanner.Token> tokenList = new ArrayList<>();
                tokenList.add(previousToken);
                tokenList.add(nextToken);
                return factory.createOperator(token.name(), new Object[]{tryParseComplex(tokenList)});
            }
        }



        // Process tokens and find reference tokens.
        for (int i = 0; i < tokens.size(); i++) {
            ComplexScanner.Token token = tokens.get(i);
            if (token.type() == ComplexScanner.TokenType.REFERENCE) {
                return (factory.createReference(token.name()));
            }
        }

        for (int i = 0; i < tokens.size(); i++) {
            ComplexScanner.Token token = tokens.get(i);
            if (token.type() == ComplexScanner.TokenType.CONST) {
                try {
                    long number = Long.parseLong(token.name());
                    return factory.createConstant(number);
                } catch (NumberFormatException e) {
                    throw new ParseException("Failed to parse constant: " + token.name());
                }
            }
        }

//        for (char character : input.toCharArray()) {
//            if (!(Character.isAlphabetic(character) || Character.isDigit(character))) {
//                throw new ParseException("Unknown input: " + input);
//            }
//        }
        if (input.isEmpty()) {
            return factory.createEmpty();
        }
        return factory.createReference(input);
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
     *
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
