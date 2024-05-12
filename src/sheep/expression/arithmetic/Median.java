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
        Arrays.sort(arguments);

        if (arguments.length % 2 == 0) {
            return (arguments[arguments.length / 2 - 1] + arguments[arguments.length / 2]) / 2;
        } else {
            return arguments[arguments.length / 2];
        }
    }
}
