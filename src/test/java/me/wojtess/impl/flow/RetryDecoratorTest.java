package me.wojtess.impl.flow;

import me.wojtess.BehaviorContext;
import me.wojtess.Node;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class RetryDecoratorTest {

    @Test
    public void testImmediateSuccess() throws Throwable {
        RetryDecorator retry = new RetryDecorator(context -> Node.Status.SUCCESS, 3);
        BehaviorContext context = new BehaviorContext();
        assert retry.tick(context).equals(Node.Status.SUCCESS);
    }

    @Test
    public void testRetriesUntilSuccess() throws Throwable {
        AtomicInteger attempt = new AtomicInteger();
        RetryDecorator retry = new RetryDecorator(
            context -> attempt.incrementAndGet() < 3 ? Node.Status.FAILURE : Node.Status.SUCCESS,
            5
        );
        BehaviorContext context = new BehaviorContext();
        assert retry.tick(context).equals(Node.Status.RUNNING); // fail 1, retry
        assert retry.tick(context).equals(Node.Status.RUNNING); // fail 2, retry
        assert retry.tick(context).equals(Node.Status.SUCCESS); // success on attempt 3
    }

    @Test
    public void testExhaustsRetriesReturnsFailure() throws Throwable {
        RetryDecorator retry = new RetryDecorator(context -> Node.Status.FAILURE, 2);
        BehaviorContext context = new BehaviorContext();
        assert retry.tick(context).equals(Node.Status.RUNNING); // retry 1
        assert retry.tick(context).equals(Node.Status.FAILURE); // retries exhausted
    }

    @Test
    public void testCounterResetsAfterExhaustion() throws Throwable {
        RetryDecorator retry = new RetryDecorator(context -> Node.Status.FAILURE, 2);
        BehaviorContext context = new BehaviorContext();
        retry.tick(context); // RUNNING
        retry.tick(context); // FAILURE — counter resets
        // Second cycle starts fresh
        assert retry.tick(context).equals(Node.Status.RUNNING);
        assert retry.tick(context).equals(Node.Status.FAILURE);
    }

    @Test
    public void testRunningDoesNotConsumeRetry() throws Throwable {
        AtomicInteger callCount = new AtomicInteger();
        RetryDecorator retry = new RetryDecorator(
            context -> {
                int n = callCount.incrementAndGet();
                if (n <= 3) return Node.Status.RUNNING;
                return Node.Status.FAILURE;
            },
            2
        );
        BehaviorContext context = new BehaviorContext();
        // calls 1-3: RUNNING (not counted as failures)
        assert retry.tick(context).equals(Node.Status.RUNNING);
        assert retry.tick(context).equals(Node.Status.RUNNING);
        assert retry.tick(context).equals(Node.Status.RUNNING);
        // calls 4,5: actual failures
        assert retry.tick(context).equals(Node.Status.RUNNING); // failure 1, retry
        assert retry.tick(context).equals(Node.Status.FAILURE); // failure 2, exhausted
    }

    @Test
    public void testEmptyChildrenReturnsFailure() throws Throwable {
        RetryDecorator retry = new RetryDecorator();
        BehaviorContext context = new BehaviorContext();
        assert retry.tick(context).equals(Node.Status.FAILURE);
    }
}
