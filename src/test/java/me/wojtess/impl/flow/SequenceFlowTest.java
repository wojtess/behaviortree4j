package me.wojtess.impl.flow;

import me.wojtess.BehaviorContext;
import me.wojtess.Node;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class SequenceFlowTest {

    @Test
    public void testAllSucceedReturnsSuccess() throws Throwable {
        SequenceFlow sequence = new SequenceFlow();
        sequence.addChildren(context -> Node.Status.SUCCESS);
        sequence.addChildren(context -> Node.Status.SUCCESS);
        sequence.addChildren(context -> Node.Status.SUCCESS);
        BehaviorContext context = new BehaviorContext();
        assert sequence.tick(context).equals(Node.Status.SUCCESS);
    }

    @Test
    public void testStopsOnRunning() throws Throwable {
        SequenceFlow sequence = new SequenceFlow();
        AtomicInteger counter = new AtomicInteger();
        sequence.addChildren(context -> Node.Status.RUNNING);
        sequence.addChildren(context -> {
            counter.getAndIncrement();
            return Node.Status.SUCCESS;
        });
        BehaviorContext context = new BehaviorContext();
        assert sequence.tick(context).equals(Node.Status.RUNNING);
        assert counter.get() == 0;
    }

    @Test
    public void testStopsOnFailure() throws Throwable {
        SequenceFlow sequence = new SequenceFlow();
        AtomicInteger counter = new AtomicInteger();
        sequence.addChildren(context -> Node.Status.FAILURE);
        sequence.addChildren(context -> {
            counter.getAndIncrement();
            return Node.Status.SUCCESS;
        });
        BehaviorContext context = new BehaviorContext();
        assert sequence.tick(context).equals(Node.Status.FAILURE);
        assert counter.get() == 0;
    }

    @Test
    public void testEmptyReturnsSuccess() throws Throwable {
        SequenceFlow sequence = new SequenceFlow();
        BehaviorContext context = new BehaviorContext();
        assert sequence.tick(context).equals(Node.Status.SUCCESS);
    }
}
