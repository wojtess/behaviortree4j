package me.wojtess.impl.flow;

import me.wojtess.BehaviorContext;
import me.wojtess.Node;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class RepeatDecoratorTest {

    @Test
    public void testRepeatsNTimes() throws Throwable {
        AtomicInteger callCount = new AtomicInteger();
        RepeatDecorator repeat = new RepeatDecorator(
            context -> { callCount.incrementAndGet(); return Node.Status.SUCCESS; },
            3
        );
        BehaviorContext context = new BehaviorContext();
        assert repeat.tick(context).equals(Node.Status.RUNNING); // 1st success
        assert repeat.tick(context).equals(Node.Status.RUNNING); // 2nd success
        assert repeat.tick(context).equals(Node.Status.SUCCESS); // 3rd success — done
        assert callCount.get() == 3;
    }

    @Test
    public void testChildRunningReturnsRunning() throws Throwable {
        RepeatDecorator repeat = new RepeatDecorator(context -> Node.Status.RUNNING, 3);
        BehaviorContext context = new BehaviorContext();
        assert repeat.tick(context).equals(Node.Status.RUNNING);
    }

    @Test
    public void testChildFailureAbortsAndReturnsFailure() throws Throwable {
        AtomicInteger calls = new AtomicInteger();
        RepeatDecorator repeat = new RepeatDecorator(
            context -> calls.getAndIncrement() < 1 ? Node.Status.SUCCESS : Node.Status.FAILURE,
            5
        );
        BehaviorContext context = new BehaviorContext();
        assert repeat.tick(context).equals(Node.Status.RUNNING); // first success
        assert repeat.tick(context).equals(Node.Status.FAILURE); // then fail — abort
    }

    @Test
    public void testCounterResetsAfterCompletion() throws Throwable {
        RepeatDecorator repeat = new RepeatDecorator(context -> Node.Status.SUCCESS, 2);
        BehaviorContext context = new BehaviorContext();
        // First cycle
        assert repeat.tick(context).equals(Node.Status.RUNNING);
        assert repeat.tick(context).equals(Node.Status.SUCCESS);
        // Second cycle starts fresh
        assert repeat.tick(context).equals(Node.Status.RUNNING);
        assert repeat.tick(context).equals(Node.Status.SUCCESS);
    }

    @Test
    public void testInfiniteRepeatNeverReturnsSuccess() throws Throwable {
        RepeatDecorator repeat = new RepeatDecorator(context -> Node.Status.SUCCESS, -1);
        BehaviorContext context = new BehaviorContext();
        for (int i = 0; i < 50; i++) {
            assert repeat.tick(context).equals(Node.Status.RUNNING);
        }
    }

    @Test
    public void testEmptyChildrenReturnsFailure() throws Throwable {
        RepeatDecorator repeat = new RepeatDecorator();
        BehaviorContext context = new BehaviorContext();
        assert repeat.tick(context).equals(Node.Status.FAILURE);
    }
}
