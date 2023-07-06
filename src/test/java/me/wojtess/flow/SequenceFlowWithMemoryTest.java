package me.wojtess.flow;

import me.wojtess.BehaviorContext;
import me.wojtess.IONode;
import me.wojtess.impl.flow.SequenceFlowWithMemory;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class SequenceFlowWithMemoryTest {

    @Test
    public void test() throws Throwable {
        SequenceFlowWithMemory sequenceFlowWithMemory = new SequenceFlowWithMemory();
        AtomicInteger counter = new AtomicInteger();
        sequenceFlowWithMemory.addChildren(((context) -> {
            counter.getAndIncrement();
            return IONode.Status.SUCCESS;
        }));
        sequenceFlowWithMemory.addChildren((context) -> IONode.Status.RUNNING);
        BehaviorContext context = new BehaviorContext();
        sequenceFlowWithMemory.tick(context);
        sequenceFlowWithMemory.tick(context);
        sequenceFlowWithMemory.tick(context);
        sequenceFlowWithMemory.tick(context);
        sequenceFlowWithMemory.tick(context);
        assert counter.get() == 1;
    }

}