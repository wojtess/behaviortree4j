package me.wojtess.impl.flow;

import me.wojtess.BehaviorContext;
import me.wojtess.Node;

import java.util.List;

/**
 * SelectorFlow is extending FlowController it runs childrens from first to last until one of them return SUCCESS or RUNNING.
 * If none of child returns SUCCESS or RUNNING, FAILURE is returning
 */
public class SelectorFlow extends FlowController {

    public SelectorFlow() {}

    public SelectorFlow(List<Node> childrens) {
        super(childrens);
    }

    @Override
    public Status tick0(BehaviorContext context) throws Throwable {
        for (Node children : this.childrens) {
            Status returnValue = children.tick(context);
            if(returnValue.equals(Status.SUCCESS)) {
                return Status.SUCCESS;
            }
            if(returnValue.equals(Status.RUNNING)) {
                return Status.RUNNING;
            }
        }
        return Status.FAILURE;
    }
}
