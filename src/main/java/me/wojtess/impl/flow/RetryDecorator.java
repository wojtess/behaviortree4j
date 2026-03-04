package me.wojtess.impl.flow;

import me.wojtess.BehaviorContext;
import me.wojtess.Input;
import me.wojtess.Node;

/**
 * RetryDecorator retries its single child node up to {@code maxRetries} times on FAILURE.
 *
 * Each outer tick executes the child at most once:
 * - Child SUCCESS  -> resets counter and returns SUCCESS
 * - Child RUNNING  -> returns RUNNING (child not done yet, does not count as a failure)
 * - Child FAILURE  -> increments counter:
 *     - if counter < maxRetries: returns RUNNING (will retry on next tick)
 *     - if counter >= maxRetries: resets counter and returns FAILURE (all retries exhausted)
 *
 * NOTE: Although this extends FlowController for serialization compatibility,
 * only the FIRST child is used. Adding more than one child is unsupported.
 */
public class RetryDecorator extends FlowController {

    @Input
    private int maxRetries = 1;

    // Cross-tick state — NOT @Input annotated so it persists between ticks
    private int failureCount = 0;

    public RetryDecorator() {}

    public RetryDecorator(Node child, int maxRetries) {
        this.maxRetries = maxRetries;
        addChildren(child);
    }

    @Override
    public Status tick0(BehaviorContext context) throws Throwable {
        if (childrens.isEmpty()) {
            return Status.FAILURE;
        }
        Status result = childrens.get(0).tick(context);

        if (result == Status.SUCCESS) {
            failureCount = 0;
            return Status.SUCCESS;
        }
        if (result == Status.RUNNING) {
            return Status.RUNNING;
        }
        // FAILURE
        failureCount++;
        if (failureCount >= maxRetries) {
            failureCount = 0;
            return Status.FAILURE;
        }
        return Status.RUNNING;
    }
}
