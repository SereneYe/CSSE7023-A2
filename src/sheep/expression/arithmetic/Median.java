package sheep.expression.arithmetic;

import sheep.expression.Expression;

import java.util.Arrays;

public class Median extends Function {
    public Median(Expression[] arguments) {
        super("MEDIAN", arguments);
    }

    @Override
    protected long perform(long[] arguments) {
        if (arguments.length == 0) {
            throw new IllegalArgumentException("No arguments provided");
        }
        long[] sortedArguments = Arrays.copyOf(arguments, arguments.length);
        Arrays.sort(sortedArguments);

        if (sortedArguments.length % 2 == 0) {
            return (sortedArguments[sortedArguments.length / 2 - 1] + sortedArguments[sortedArguments.length / 2]) / 2;
        } else {
            return sortedArguments[sortedArguments.length / 2];
        }
    }
}
