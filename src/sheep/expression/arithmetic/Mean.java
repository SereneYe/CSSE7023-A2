package sheep.expression.arithmetic;

import sheep.expression.Expression;

public class Mean extends Function {
    public Mean(Expression[] arguments) {
        super("MEAN", arguments);
    }

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
