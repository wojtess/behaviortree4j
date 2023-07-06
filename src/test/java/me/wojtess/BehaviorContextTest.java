package me.wojtess;

import org.junit.Test;

public class BehaviorContextTest {

    @Test
    public void value() {
        BehaviorContext context = new BehaviorContext();
        context.putValue("test", 21);
        assert ((int) context.getValue("test").get()) == 21;
        assert context.getValue("test", Integer.class).get() == 21;
        assert context.getValue("test", Float.class).isEmpty();
        assert context.getAndRemoveValue("test", Float.class).isEmpty();
        assert context.getLocalValue("test", Integer.class).get() == 21;
        assert context.getAndRemoveValue("test", Integer.class).get() == 21;
        assert context.getLocalValue("test", Integer.class).isEmpty();
    }

    @Test
    public void child() {
        BehaviorContext context = new BehaviorContext();
        context.putValue("test1", 1);
        BehaviorContext child = context.getChild();
        context.putValue("test2", 2);
        context.putValue("test3", 3);
        child.putValue("test3", 22);

        assert child.getValue("test1", Integer.class).get() == 1;
        assert child.getValue("test2", Integer.class).get() == 2;
        assert child.getValue("test3", Integer.class).get() == 22;

        BehaviorContext child1 = context.getChild();
        assert child1.getValue("test1", Integer.class).get() == 1;
        assert child1.getValue("test2", Integer.class).get() == 2;
        assert child1.getValue("test3", Integer.class).get() == 3;
    }
}