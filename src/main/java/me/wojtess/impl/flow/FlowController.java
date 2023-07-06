package me.wojtess.impl.flow;

import me.wojtess.BehaviorContext;
import me.wojtess.IONode;
import me.wojtess.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * FlowController is abstract class that holds context and childrens to execute.
 * Subclasses are executing these childrens
 */
public abstract class FlowController extends IONode {

    protected List<Node> childrens = new ArrayList<>();

    public FlowController() {}

    public FlowController(List<Node> childrens) {
        this.childrens = childrens;
    }

    public List<Node> getChildrens() {
        return new ArrayList<>(childrens);
    }

    public FlowController addChildren(Node IONode) {
        childrens.add(IONode);
        return this;
    }

    @Override
    public Status abstractTick(BehaviorContext context) throws Throwable {
        return tick0(context);
    }

    public abstract Status tick0(BehaviorContext context) throws Throwable;
}
