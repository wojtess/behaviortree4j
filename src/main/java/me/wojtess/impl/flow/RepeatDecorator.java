package me.wojtess.impl.flow;

import me.wojtess.BehaviorContext;
import me.wojtess.Input;
import me.wojtess.Node;

/**
 * RepeatDecorator repeats its single child node up to {@code maxRepeats} times.
 * A value of -1 for maxRepeats means repeat indefinitely.
 *
 * Each outer tick executes the child at most once:
 * - Child RUNNING  -> returns RUNNING (wait for child to finish)
 * - Child FAILURE  -> resets counter and returns FAILURE (abort)
 * - Child SUCCESS  -> increments counter; if counter reached maxRepeats, resets and returns SUCCESS;
 *                     otherwise returns RUNNING (more repetitions needed next tick)
 *
 * NOTE: Although this extends FlowController for serialization compatibility,
 * only the FIRST child is used. Adding more than one child is unsupported.
 */
public class RepeatDecorator extends FlowController {

    @Input
    private int maxRepeats = 1;

    // Cross-tick state — NOT @Input annotated so it persists between ticks
    private int successCount = 0;

    public RepeatDecorator() {}

    public RepeatDecorator(Node child, int maxRepeats) {
        this.maxRepeats = maxRepeats;
        addChildren(child);
    }

    @Override
    public Status tick0(BehaviorContext context) throws Throwable {
        if (childrens.isEmpty()) {
            return Status.FAILURE;
        }
        Status result = childrens.get(0).tick(context);

        if (result == Status.RUNNING) {
            return Status.RUNNING;
        }
        if (result == Status.FAILURE) {
            successCount = 0;
            return Status.FAILURE;
        }
        // SUCCESS
        successCount++;
        if (maxRepeats != -1 && successCount >= maxRepeats) {
            successCount = 0;
            return Status.SUCCESS;
        }
        return Status.RUNNING;
    }
}
