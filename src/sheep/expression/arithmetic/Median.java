package sheep.expression.arithmetic;

import sheep.expression.Expression;

import java.util.Arrays;

/**
 * Represents the Median operation that calculates the median value from a
 * sequence of sub-expressions.
 */
public class Median extends Function {

    /**
     * Construct a new median operation.
     *
     * @param arguments A sequence of sub-expressions to perform the operation upon.
     * @return A median expression.
     * @requires arguments.length > 0
     */
    public Median(Expression[] arguments) {
        super("MEDIAN", arguments);
    }

    /**
     * Performs the median operation on an array of long values.
     *
     * @param arguments An array of long values on which the median operation is to be performed.
     * @return The median value from the given array of long values.
     * @throws IllegalArgumentException if no arguments are provided.
     */
    @Override
    protected long perform(long[] arguments) {
        if (arguments.length == 0) {
            throw new IllegalArgumentException("No arguments provided");
        }
        long[] sortedArguments = Arrays.copyOf(arguments, arguments.length);
        Arrays.sort(sortedArguments);

        if (sortedArguments.length % 2 == 0) {
            return (sortedArguments[sortedArguments.length / 2 - 1] +
                    sortedArguments[sortedArguments.length / 2]) / 2;
        } else {
            return sortedArguments[sortedArguments.length / 2];
        }
    }
}
