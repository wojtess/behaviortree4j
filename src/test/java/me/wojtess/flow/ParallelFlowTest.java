package me.wojtess.flow;

import me.wojtess.BehaviorContext;
import me.wojtess.IONode;
import me.wojtess.impl.flow.ParallelFlow;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class ParallelFlowTest {

    @Test
    public void allSucceed_returnsSuccess() throws Throwable {
        ParallelFlow parallel = new ParallelFlow();
        parallel.addChildren(ctx -> IONode.Status.SUCCESS);
        parallel.addChildren(ctx -> IONode.Status.SUCCESS);
        assert parallel.tick(new BehaviorContext()) == IONode.Status.SUCCESS;
    }

    @Test
    public void anyFails_thresholdNotMet_returnsFailure() throws Throwable {
        // requiredSuccesses=2, but one fails and one succeeds -> can't reach 2
        ParallelFlow parallel = new ParallelFlow();
        parallel.addChildren(ctx -> IONode.Status.SUCCESS);
        parallel.addChildren(ctx -> IONode.Status.FAILURE);
        // default requiredSuccesses=1, one success -> SUCCESS
        assert parallel.tick(new BehaviorContext()) == IONode.Status.SUCCESS;
    }

    @Test
    public void thresholdNotReachable_returnsFailure() throws Throwable {
        // requiredSuccesses=2, both fail -> unreachable
        ParallelFlow parallel = new ParallelFlow();
        parallel.addChildren(ctx -> IONode.Status.FAILURE);
        parallel.addChildren(ctx -> IONode.Status.FAILURE);
        assert parallel.tick(new BehaviorContext()) == IONode.Status.FAILURE;
    }

    @Test
    public void oneRunning_oneSuccess_thresholdTwo_returnsRunning() throws Throwable {
        ParallelFlow parallel = new ParallelFlow(java.util.List.of(), 2);
        parallel.addChildren(ctx -> IONode.Status.SUCCESS);
        parallel.addChildren(ctx -> IONode.Status.RUNNING);
        // 1 success, 1 running -> could still reach 2 -> RUNNING
        assert parallel.tick(new BehaviorContext()) == IONode.Status.RUNNING;
    }

    @Test
    public void allChildrenAreTickedEveryTime() throws Throwable {
        AtomicInteger tickCount = new AtomicInteger();
        ParallelFlow parallel = new ParallelFlow();
        parallel.addChildren(ctx -> { tickCount.incrementAndGet(); return IONode.Status.SUCCESS; });
        parallel.addChildren(ctx -> { tickCount.incrementAndGet(); return IONode.Status.FAILURE; });
        parallel.addChildren(ctx -> { tickCount.incrementAndGet(); return IONode.Status.RUNNING; });
        BehaviorContext ctx = new BehaviorContext();
        parallel.tick(ctx);
        parallel.tick(ctx);
        // All 3 children ticked on each of the 2 calls
        assert tickCount.get() == 6;
    }
}
