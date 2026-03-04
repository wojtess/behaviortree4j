package me.wojtess.flow;

import me.wojtess.BehaviorContext;
import me.wojtess.IONode;
import me.wojtess.impl.flow.RepeatDecorator;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class RepeatDecoratorTest {

    @Test
    public void repeatsNTimes() throws Throwable {
        AtomicInteger callCount = new AtomicInteger();
        RepeatDecorator repeat = new RepeatDecorator(
            ctx -> { callCount.incrementAndGet(); return IONode.Status.SUCCESS; },
            3
        );
        BehaviorContext ctx = new BehaviorContext();
        assert repeat.tick(ctx) == IONode.Status.RUNNING; // 1st success
        assert repeat.tick(ctx) == IONode.Status.RUNNING; // 2nd success
        assert repeat.tick(ctx) == IONode.Status.SUCCESS; // 3rd success -> done
        assert callCount.get() == 3;
    }

    @Test
    public void childRunning_returnsRunning() throws Throwable {
        RepeatDecorator repeat = new RepeatDecorator(ctx -> IONode.Status.RUNNING, 3);
        assert repeat.tick(new BehaviorContext()) == IONode.Status.RUNNING;
    }

    @Test
    public void childFails_returnsFailure() throws Throwable {
        AtomicInteger calls = new AtomicInteger();
        RepeatDecorator repeat = new RepeatDecorator(
            ctx -> calls.getAndIncrement() < 1 ? IONode.Status.SUCCESS : IONode.Status.FAILURE,
            5
        );
        BehaviorContext ctx = new BehaviorContext();
        assert repeat.tick(ctx) == IONode.Status.RUNNING; // first success
        assert repeat.tick(ctx) == IONode.Status.FAILURE; // then fail -> abort
    }

    @Test
    public void counterResetsAfterCompletion() throws Throwable {
        RepeatDecorator repeat = new RepeatDecorator(ctx -> IONode.Status.SUCCESS, 2);
        BehaviorContext ctx = new BehaviorContext();
        assert repeat.tick(ctx) == IONode.Status.RUNNING;
        assert repeat.tick(ctx) == IONode.Status.SUCCESS;
        // Second cycle
        assert repeat.tick(ctx) == IONode.Status.RUNNING;
        assert repeat.tick(ctx) == IONode.Status.SUCCESS;
    }

    @Test
    public void infiniteRepeat_neverSucceeds() throws Throwable {
        RepeatDecorator repeat = new RepeatDecorator(ctx -> IONode.Status.SUCCESS, -1);
        BehaviorContext ctx = new BehaviorContext();
        for (int i = 0; i < 50; i++) {
            assert repeat.tick(ctx) == IONode.Status.RUNNING;
        }
    }
}
