package me.wojtess;

import me.wojtess.impl.flow.FlowController;
import me.wojtess.impl.flow.SelectorFlow;
import me.wojtess.impl.flow.SequenceFlow;
import org.junit.Test;

public class BehaviorTreeSystemTest {

    @Test
    public void sequenceTest() throws Throwable {
        BehaviorTreeSystem system = new BehaviorTreeSystem();
        {
            FlowController rootNode = new SequenceFlow();
            rootNode.addChildren((context) -> IONode.Status.SUCCESS);
            rootNode.addChildren((context) -> IONode.Status.FAILURE);
            rootNode.addChildren((context) -> {
                //this should never execute
                assert false;
                return IONode.Status.FAILURE;
            });
            system.setRootNode(rootNode);
        }
        system.tick();
        system.tick();
        system.tick();
    }

    @Test
    public void selectorTest() throws Throwable {
        BehaviorTreeSystem system = new BehaviorTreeSystem();
        {
            FlowController rootNode = new SelectorFlow();
            rootNode.addChildren((context) -> IONode.Status.FAILURE);
            rootNode.addChildren((context) -> IONode.Status.SUCCESS);
            rootNode.addChildren((context) -> {
                //this should never execute
                assert false;
                return IONode.Status.FAILURE;
            });
            system.setRootNode(rootNode);
        }
        system.tick();
        system.tick();
        system.tick();
    }

    @Test
    public void contextTest() throws Throwable {
        BehaviorTreeSystem system = new BehaviorTreeSystem();
        {
            FlowController rootNode = new SequenceFlow();
            rootNode.addChildren((context) -> {
                context.putValue("test1", 1);
                return IONode.Status.SUCCESS;
            });
            rootNode.addChildren(new SequenceFlow()
                        .addChildren((context) -> {
                            context.putValue("test", 1);
                            return IONode.Status.SUCCESS;
                        })
                        .addChildren(((context) -> {
                            assert context.getLocalValue("test2").isEmpty();
                            assert context.getValue("test1", Integer.class).get() == 1;
                            assert context.getValue("test", Integer.class).get() == 1;
                            return IONode.Status.SUCCESS;
                        }))
            );
            rootNode.addChildren((context) -> {
                assert context.getValue("test").isEmpty();
                context.putValue("test2", 1);
                return IONode.Status.SUCCESS;
            });
            system.setRootNode(rootNode);
        }
        system.tick();
        system.tick();
    }

}