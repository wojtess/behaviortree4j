package me.wojtess.impl.flow;

import me.wojtess.BehaviorContext;
import me.wojtess.Node;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class SelectorFlowTest {

    @Test
    public void testAllFailReturnsFailure() throws Throwable {
        SelectorFlow selector = new SelectorFlow();
        selector.addChildren(context -> Node.Status.FAILURE);
        selector.addChildren(context -> Node.Status.FAILURE);
        selector.addChildren(context -> Node.Status.FAILURE);
        BehaviorContext context = new BehaviorContext();
        assert selector.tick(context).equals(Node.Status.FAILURE);
    }

    @Test
    public void testStopsOnSuccess() throws Throwable {
        SelectorFlow selector = new SelectorFlow();
        AtomicInteger counter = new AtomicInteger();
        selector.addChildren(context -> Node.Status.SUCCESS);
        selector.addChildren(context -> {
            counter.getAndIncrement();
            return Node.Status.FAILURE;
        });
        BehaviorContext context = new BehaviorContext();
        assert selector.tick(context).equals(Node.Status.SUCCESS);
        assert counter.get() == 0;
    }

    @Test
    public void testStopsOnRunning() throws Throwable {
        SelectorFlow selector = new SelectorFlow();
        AtomicInteger counter = new AtomicInteger();
        selector.addChildren(context -> Node.Status.RUNNING);
        selector.addChildren(context -> {
            counter.getAndIncrement();
            return Node.Status.FAILURE;
        });
        BehaviorContext context = new BehaviorContext();
        assert selector.tick(context).equals(Node.Status.RUNNING);
        assert counter.get() == 0;
    }

    @Test
    public void testEmptyReturnsFailure() throws Throwable {
        SelectorFlow selector = new SelectorFlow();
        BehaviorContext context = new BehaviorContext();
        assert selector.tick(context).equals(Node.Status.FAILURE);
    }
}
