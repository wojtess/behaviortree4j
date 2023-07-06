package me.wojtess.impl.decorator;

import me.wojtess.BehaviorContext;
import me.wojtess.Node;

/**
 * InverterDecorator is changing return value from children from SUCCESS to FAILURE and from FAILURE to SUCCESS
 */
public class InverterDecorator extends Decorator {

    public InverterDecorator(Node children) {
        super(children);
    }

    @Override
    public Status tick(BehaviorContext context) throws Throwable {
        Status returnValue = children.tick(context);
        if(returnValue.equals(Status.SUCCESS)) {
            return Status.FAILURE;
        }
        if(returnValue.equals(Status.FAILURE)) {
            return Status.SUCCESS;
        }
        return returnValue;
    }
}
