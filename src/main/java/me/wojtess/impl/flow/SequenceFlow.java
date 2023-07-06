package me.wojtess.impl.flow;

import me.wojtess.BehaviorContext;
import me.wojtess.Node;

import java.util.List;

/**
 * Sequence selector runs childrens from first to last
 * if one return RUNNING or FAILURE, executing is stopped and RUNNING or FAILURE is returned
 */
public class SequenceFlow extends FlowController {

    public SequenceFlow() {
    }

    public SequenceFlow(List<Node> childrens) {
        super(childrens);
    }

    @Override
    public Status tick0(BehaviorContext context) throws Throwable {
        for (Node children : this.childrens) {
            Status returnValue = children.tick(context);
            if(returnValue.equals(Status.RUNNING)) {
                return Status.RUNNING;
            }
            if(returnValue.equals(Status.FAILURE)) {
                return Status.FAILURE;
            }
        }
        return Status.SUCCESS;
    }
}
