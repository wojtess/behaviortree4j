package me.wojtess;

import org.junit.Test;

public class IONodeTest {

    public static class ThrowingNode extends IONode {
        @Input
        private String someField;

        @Override
        public Status abstractTick(BehaviorContext context) throws Throwable {
            throw new RuntimeException("simulated error");
        }
    }

    public static class StringInputNode extends IONode {
        @Input
        private String value;

        public String getValue() {
            return value;
        }

        @Override
        public Status abstractTick(BehaviorContext context) {
            return Status.SUCCESS;
        }
    }

    public static class OutputNode extends IONode {
        @Output
        private String result;

        @Override
        public Status abstractTick(BehaviorContext context) {
            result = "hello";
            return Status.SUCCESS;
        }
    }

    public static class ContextInputNode extends IONode {
        @Input
        private String value;

        public String getValue() {
            return value;
        }

        @Override
        public Status abstractTick(BehaviorContext context) {
            return Status.SUCCESS;
        }
    }

    @Test
    public void testExceptionInAbstractTickReturnsFailure() throws Throwable {
        ThrowingNode node = new ThrowingNode();
        BehaviorContext context = new BehaviorContext();
        assert node.tick(context).equals(Node.Status.FAILURE);
    }

    @Test
    public void testLiteralStringInput() throws Throwable {
        StringInputNode node = new StringInputNode();
        node.putInput("value", "hello");
        BehaviorContext context = new BehaviorContext();
        node.tick(context);
        assert "hello".equals(node.getValue());
    }

    @Test
    public void testOutputWrittenToContext() throws Throwable {
        OutputNode node = new OutputNode();
        node.putOutput("result", "myKey");
        BehaviorContext context = new BehaviorContext();
        node.tick(context);
        assert context.getValue("myKey", String.class).isPresent();
        assert context.getValue("myKey", String.class).get().equals("hello");
    }

    @Test
    public void testContextVariableInput() throws Throwable {
        ContextInputNode node = new ContextInputNode();
        node.putInput("value", "{myVar}");
        BehaviorContext context = new BehaviorContext();
        context.putValue("{myVar}", "fromContext");
        node.tick(context);
        assert "fromContext".equals(node.getValue());
    }
}
