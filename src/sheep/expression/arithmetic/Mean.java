package sheep.expression.arithmetic;

import sheep.expression.Expression;

public class Mean extends Operation {
    public Mean(Expression[] arguments) {
        super("MEAN", arguments);
    }

    @Override
    protected long perform(long[] arguments) {
        return 0;
    }
}
