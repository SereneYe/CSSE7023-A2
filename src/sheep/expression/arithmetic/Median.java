package sheep.expression.arithmetic;

import sheep.expression.Expression;

public class Median extends Operation {
    public Median(Expression[] arguments) {
        super("MEDIAN", arguments);
    }

    @Override
    protected long perform(long[] arguments) {
        return 0;
    }
}
