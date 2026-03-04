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

    @Test
    public void testResetsOnSuccess() throws Throwable {
        SequenceFlowWithMemory sequenceFlowWithMemory = new SequenceFlowWithMemory();
        AtomicInteger counter = new AtomicInteger();
        sequenceFlowWithMemory.addChildren(context -> {
            counter.getAndIncrement();
            return IONode.Status.SUCCESS;
        });
        sequenceFlowWithMemory.addChildren(context -> IONode.Status.SUCCESS);
        BehaviorContext context = new BehaviorContext();
        // First tick: all succeed, index resets to 0, returns SUCCESS
        assert sequenceFlowWithMemory.tick(context).equals(IONode.Status.SUCCESS);
        assert counter.get() == 1;
        // Second tick: starts from 0 again because SUCCESS reset the index
        assert sequenceFlowWithMemory.tick(context).equals(IONode.Status.SUCCESS);
        assert counter.get() == 2;
    }

    @Test
    public void testFailureDoesNotSaveIndex() throws Throwable {
        SequenceFlowWithMemory sequenceFlowWithMemory = new SequenceFlowWithMemory();
        AtomicInteger counter = new AtomicInteger();
        sequenceFlowWithMemory.addChildren(context -> {
            counter.getAndIncrement();
            return IONode.Status.SUCCESS;
        });
        sequenceFlowWithMemory.addChildren(context -> IONode.Status.FAILURE);
        BehaviorContext context = new BehaviorContext();
        // First tick: second child fails, index not saved, returns FAILURE
        assert sequenceFlowWithMemory.tick(context).equals(IONode.Status.FAILURE);
        assert counter.get() == 1;
        // Second tick: starts from 0 again (FAILURE does not save index)
        assert sequenceFlowWithMemory.tick(context).equals(IONode.Status.FAILURE);
        assert counter.get() == 2;
    }

}
