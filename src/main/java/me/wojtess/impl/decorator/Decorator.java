package me.wojtess.impl.decorator;

import me.wojtess.Node;

/**
 * Decorator having one child, it is changing node return value
 */
public abstract class Decorator implements Node {

    protected final Node children;

    public Decorator(Node children) {
        this.children = children;
    }

}
