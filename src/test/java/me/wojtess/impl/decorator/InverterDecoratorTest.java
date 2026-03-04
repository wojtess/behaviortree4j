package me.wojtess.impl.decorator;

import me.wojtess.BehaviorContext;
import me.wojtess.Node;
import org.junit.Test;

public class InverterDecoratorTest {

    @Test
    public void testSuccessInverted() throws Throwable {
        InverterDecorator inverter = new InverterDecorator(context -> Node.Status.SUCCESS);
        BehaviorContext context = new BehaviorContext();
        assert inverter.tick(context).equals(Node.Status.FAILURE);
    }

    @Test
    public void testFailureInverted() throws Throwable {
        InverterDecorator inverter = new InverterDecorator(context -> Node.Status.FAILURE);
        BehaviorContext context = new BehaviorContext();
        assert inverter.tick(context).equals(Node.Status.SUCCESS);
    }

    @Test
    public void testRunningPassthrough() throws Throwable {
        InverterDecorator inverter = new InverterDecorator(context -> Node.Status.RUNNING);
        BehaviorContext context = new BehaviorContext();
        assert inverter.tick(context).equals(Node.Status.RUNNING);
    }
}
