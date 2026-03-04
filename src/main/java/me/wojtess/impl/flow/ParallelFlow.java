package me.wojtess.impl.flow;

import me.wojtess.BehaviorContext;
import me.wojtess.Input;
import me.wojtess.Node;

import java.util.List;

/**
 * ParallelFlow ticks ALL children every tick regardless of individual results.
 * It succeeds when at least {@code requiredSuccesses} children returned SUCCESS.
 * It fails when enough children have failed that the success threshold is no longer reachable.
 * Otherwise it returns RUNNING.
 *
 * Common configurations:
 * - requiredSuccesses=1: succeed if any child succeeds (OR-like)
 * - requiredSuccesses=N (all children): succeed only if all children succeed (AND-like)
 */
public class ParallelFlow extends FlowController {

    @Input
    private int requiredSuccesses = 1;

    public ParallelFlow() {}

    public ParallelFlow(List<Node> childrens, int requiredSuccesses) {
        this.requiredSuccesses = requiredSuccesses;
        for (Node child : childrens) {
            addChildren(child);
        }
    }

    @Override
    public Status tick0(BehaviorContext context) throws Throwable {
        int successCount = 0;
        int failureCount = 0;

        for (Node child : this.childrens) {
            Status result = child.tick(context);
            if (result == Status.SUCCESS) {
                successCount++;
            } else if (result == Status.FAILURE) {
                failureCount++;
            }
        }

        if (successCount >= requiredSuccesses) {
            return Status.SUCCESS;
        }

        // If remaining possible successes (RUNNING nodes) cannot reach threshold, fail
        int running = childrens.size() - successCount - failureCount;
        if (successCount + running < requiredSuccesses) {
            return Status.FAILURE;
        }

        return Status.RUNNING;
    }
}
