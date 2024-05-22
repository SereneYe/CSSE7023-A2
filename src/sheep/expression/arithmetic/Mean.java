package sheep.expression.arithmetic;

import sheep.expression.Expression;

/**
 * Represents a function expression that calculates the mean of an array of long values.
 */
public class Mean extends Function {

    /**
     * Constructs a new Mean object with the specified arguments.
     *
     * @param arguments An array of {@code Expression} objects that
     *                  represents the arguments to the Mean function.
     * @return A median expression.
     * @requires arguments.length > 0
     */
    public Mean(Expression[] arguments) {
        super("MEAN", arguments);
    }

    /**
     * Calculates the mean of an array of long values.
     *
     * @param arguments An array of long values.
     * @return The mean value of the array.
     * @throws IllegalArgumentException if no arguments are provided.
     */
    @Override
    protected long perform(long[] arguments) {
        if (arguments.length == 0) {
            throw new IllegalArgumentException("No arguments provided");
        }

        long sum = 0;
        for (long arg : arguments) {
            sum += arg;
        }

        return sum / arguments.length;
    }
}
