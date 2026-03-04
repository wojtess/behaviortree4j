package me.wojtess.impl.flow;

import me.wojtess.BehaviorContext;
import me.wojtess.Node;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelFlowTest {

    @Test
    public void testAllSucceedReturnsSuccess() throws Throwable {
        ParallelFlow parallel = new ParallelFlow();
        parallel.addChildren(context -> Node.Status.SUCCESS);
        parallel.addChildren(context -> Node.Status.SUCCESS);
        BehaviorContext context = new BehaviorContext();
        assert parallel.tick(context).equals(Node.Status.SUCCESS);
    }

    @Test
    public void testAllFailReturnsFailure() throws Throwable {
        ParallelFlow parallel = new ParallelFlow();
        parallel.addChildren(context -> Node.Status.FAILURE);
        parallel.addChildren(context -> Node.Status.FAILURE);
        BehaviorContext context = new BehaviorContext();
        assert parallel.tick(context).equals(Node.Status.FAILURE);
    }

    @Test
    public void testOnSuccessThresholdOneWithMixedResults() throws Throwable {
        // requiredSuccesses=1 (default): one success is enough
        ParallelFlow parallel = new ParallelFlow();
        parallel.addChildren(context -> Node.Status.SUCCESS);
        parallel.addChildren(context -> Node.Status.FAILURE);
        BehaviorContext context = new BehaviorContext();
        assert parallel.tick(context).equals(Node.Status.SUCCESS);
    }

    @Test
    public void testRunningWhenThresholdUnreachedButPossible() throws Throwable {
        // requiredSuccesses=2: 1 success + 1 running can still reach 2
        ParallelFlow parallel = new ParallelFlow(List.of(), 2);
        parallel.addChildren(context -> Node.Status.SUCCESS);
        parallel.addChildren(context -> Node.Status.RUNNING);
        BehaviorContext context = new BehaviorContext();
        assert parallel.tick(context).equals(Node.Status.RUNNING);
    }

    @Test
    public void testFailureWhenThresholdUnreachable() throws Throwable {
        // requiredSuccesses=2: 1 success + 1 failure = impossible to reach 2
        ParallelFlow parallel = new ParallelFlow(List.of(), 2);
        parallel.addChildren(context -> Node.Status.SUCCESS);
        parallel.addChildren(context -> Node.Status.FAILURE);
        BehaviorContext context = new BehaviorContext();
        assert parallel.tick(context).equals(Node.Status.FAILURE);
    }

    @Test
    public void testAllChildrenAreTickedEveryTime() throws Throwable {
        AtomicInteger tickCount = new AtomicInteger();
        ParallelFlow parallel = new ParallelFlow();
        parallel.addChildren(context -> { tickCount.incrementAndGet(); return Node.Status.SUCCESS; });
        parallel.addChildren(context -> { tickCount.incrementAndGet(); return Node.Status.FAILURE; });
        parallel.addChildren(context -> { tickCount.incrementAndGet(); return Node.Status.RUNNING; });
        BehaviorContext context = new BehaviorContext();
        parallel.tick(context);
        parallel.tick(context);
        // All 3 children must be ticked on each call
        assert tickCount.get() == 6;
    }

    @Test
    public void testEmptyChildrenReturnsFailure() throws Throwable {
        // No successes possible with empty children, threshold=1 is unreachable
        ParallelFlow parallel = new ParallelFlow();
        BehaviorContext context = new BehaviorContext();
        assert parallel.tick(context).equals(Node.Status.FAILURE);
    }
}
