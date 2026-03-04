package me.wojtess.flow;

import me.wojtess.BehaviorContext;
import me.wojtess.IONode;
import me.wojtess.impl.flow.RetryDecorator;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class RetryDecoratorTest {

    @Test
    public void retriesUntilSuccess() throws Throwable {
        AtomicInteger attempt = new AtomicInteger();
        RetryDecorator retry = new RetryDecorator(
            ctx -> attempt.incrementAndGet() < 3 ? IONode.Status.FAILURE : IONode.Status.SUCCESS,
            5
        );
        BehaviorContext ctx = new BehaviorContext();
        assert retry.tick(ctx) == IONode.Status.RUNNING; // fail 1
        assert retry.tick(ctx) == IONode.Status.RUNNING; // fail 2
        assert retry.tick(ctx) == IONode.Status.SUCCESS; // success on attempt 3
    }

    @Test
    public void exhaustsRetries_returnsFailure() throws Throwable {
        RetryDecorator retry = new RetryDecorator(ctx -> IONode.Status.FAILURE, 2);
        BehaviorContext ctx = new BehaviorContext();
        assert retry.tick(ctx) == IONode.Status.RUNNING; // retry 1
        assert retry.tick(ctx) == IONode.Status.FAILURE; // retries exhausted
    }

    @Test
    public void counterResetsAfterExhaustion() throws Throwable {
        RetryDecorator retry = new RetryDecorator(ctx -> IONode.Status.FAILURE, 2);
        BehaviorContext ctx = new BehaviorContext();
        retry.tick(ctx); // RUNNING
        retry.tick(ctx); // FAILURE (exhausted, reset)
        // second cycle
        assert retry.tick(ctx) == IONode.Status.RUNNING;
        assert retry.tick(ctx) == IONode.Status.FAILURE;
    }

    @Test
    public void runningDoesNotConsumeRetry() throws Throwable {
        AtomicInteger callCount = new AtomicInteger();
        RetryDecorator retry = new RetryDecorator(
            ctx -> {
                int n = callCount.incrementAndGet();
                if (n <= 3) return IONode.Status.RUNNING;
                return IONode.Status.FAILURE;
            },
            2
        );
        BehaviorContext ctx = new BehaviorContext();
        // calls 1-3: RUNNING (not counted as failures)
        assert retry.tick(ctx) == IONode.Status.RUNNING;
        assert retry.tick(ctx) == IONode.Status.RUNNING;
        assert retry.tick(ctx) == IONode.Status.RUNNING;
        // calls 4,5: actual failures
        assert retry.tick(ctx) == IONode.Status.RUNNING; // failure 1, retry
        assert retry.tick(ctx) == IONode.Status.FAILURE; // failure 2, exhausted
    }

    @Test
    public void immediateSuccess() throws Throwable {
        RetryDecorator retry = new RetryDecorator(ctx -> IONode.Status.SUCCESS, 3);
        assert retry.tick(new BehaviorContext()) == IONode.Status.SUCCESS;
    }
}
