package me.wojtess.impl.flow;

import me.wojtess.BehaviorContext;
import me.wojtess.Node;

import java.util.List;

/**
 * Same as SequenceFlow but this class remembers when child returns RUNNING and is starting from this child.
 * This doesnt execute nodes that was executed before node that returned RUNNING.
 */
public class SequenceFlowWithMemory extends FlowController {

    private int index;

    public SequenceFlowWithMemory() {
    }

    public SequenceFlowWithMemory(List<Node> childrens, int index) {
        super(childrens);
        this.index = index;
    }

    @Override
    public Status tick0(BehaviorContext context) throws Throwable {
        for (int i = index; i < this.childrens.size(); i++) {
            var children = this.childrens.get(i);
            Status returnValue = children.tick(context);
            if(returnValue.equals(Status.RUNNING)) {
                index = i;
                return Status.RUNNING;
            }
            if(returnValue.equals(Status.FAILURE)) {
                return Status.FAILURE;
            }
        }
        index = 0;
        return Status.SUCCESS;
    }
}
