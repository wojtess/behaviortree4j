package me.wojtess;

import java.util.Optional;

public class BehaviorTreeSystem {

    private Node rootNode = null;
    private final BehaviorContext context = new BehaviorContext();

    public BehaviorTreeSystem() {

    }
    protected void tick() throws Throwable{
        if(rootNode != null) {
            rootNode.tick(context);
        }
    }

    public Optional<Node> getRootNode() {
        return Optional.ofNullable(rootNode);
    }

    public void setRootNode(Node rootNode) {
        this.rootNode = rootNode;
    }
}
