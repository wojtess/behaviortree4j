package me.wojtess;

import me.wojtess.impl.flow.SelectorFlow;
import org.junit.Test;

import java.util.List;

public class EncoderDecoderBehaviorTreeTest {

    public static class TestNode1 extends IONode {

        @Input
        private String someInputNameInCode;

        @Output
        private String someOutputNameInCode;

        @Override
        public Status abstractTick(BehaviorContext context) {
            return Status.SUCCESS;
        }

    }

    public static class TestNode2 implements Node {

        @Override
        public Status tick(BehaviorContext context) {
            return Status.SUCCESS;
        }
    }

    @Test
    public void testDecode() throws EncoderDecoderBehaviorTree.UnknownTypeException, EncoderDecoderBehaviorTree.WrongConstructorException {
        String input = """
                {
                  "childrens": [
                    {
                      "type": "TestNode2"
                    },
                    {
                      "input": {
                        "someInputNameInCode": "{some_value}"
                      },
                      "output": {
                        "someOutputNameInCode": "null"
                      },
                      "type": "TestNode1"
                    }
                  ],
                  "input": {},
                  "output": {},
                  "type": "SelectorFlow"
                }""";
        assert EncoderDecoderBehaviorTree.encode(EncoderDecoderBehaviorTree.decode(input, List.of(
                TestNode1.class,
                TestNode2.class
        ))).equals(input);
    }

    public static record ComplexValue(String someValue, int otherValue) {

    }

    public static class TestNode3 extends IONode {

        @Output
        private String outputValue;

        @Output
        private ComplexValue value;

        @Override
        public Status abstractTick(BehaviorContext context) {
            outputValue = "secret";
            value = new ComplexValue("1234", 1234);
            return Status.SUCCESS;
        }

    }

    public static class TestNode4 extends IONode {

        @Input
        private String someInput;

        @Input
        private ComplexValue value;

        @Override
        public Status abstractTick(BehaviorContext context) {
            assert someInput.equals("secret");
            assert value.someValue.equals("1234");
            assert value.otherValue == 1234;
            return Status.SUCCESS;
        }

    }

    @Test
    public void testDecodeAndExecution() throws Throwable {
        String input = """
                {
                  "childrens": [
                    {
                      "output": {
                        "outputValue": "{some_value}",
                        "value": "{complex_value}"
                      },
                      "type": "TestNode3"
                    },
                    {
                      "input": {
                        "someInput": "{some_value}",
                        "value": "{complex_value}"
                      },
                      "type": "TestNode4"
                    }
                  ],
                  "input": {},
                  "output": {},
                  "type": "SequenceFlow"
                }""";
        Node root = EncoderDecoderBehaviorTree.decode(input, List.of(
                TestNode3.class,
                TestNode4.class
        ));
        BehaviorContext context = new BehaviorContext();
        context.putValue("{some_value}", "aaa");
        root.tick(context);
        root.tick(context);
    }

    public static class TestNode5 extends IONode {

        @Input
        private String someInput;

        @Input
        private float floatValue;

        @Input
        private double doubleValue;

        @Input
        private int intValue;

        @Input
        private boolean booleanValue;

        @Override
        public Status abstractTick(BehaviorContext context) {
            assert someInput.equals("s3cr3t");
            assert floatValue == 2.4f;
            assert doubleValue == 3.2d;
            assert intValue == 1;
            assert booleanValue;
            return Status.SUCCESS;
        }

    }

    @Test
    public void testDecodeAndExecutionWithArguments() throws Throwable {
        String input = """
                {
                  "input": {
                    "someInput": "s3cr3t",
                    "floatValue": 2.4,
                    "doubleValue": 3.2,
                    "intValue": 1,
                    "booleanValue": true
                  },
                  "output": {},
                  "type": "TestNode5"
                }""";
        Node root = EncoderDecoderBehaviorTree.decode(input, List.of(
                TestNode5.class
        ));
        BehaviorContext context = new BehaviorContext();
        root.tick(context);
        root.tick(context);
    }

    @Test
    public void testEncode() {
        Node root = new SelectorFlow()
                .addChildren(new TestNode2())
                .addChildren(new TestNode1());
        assert EncoderDecoderBehaviorTree.encode(root).equals("""
                {
                  "childrens": [
                    {
                      "type": "TestNode2"
                    },
                    {
                      "input": {
                        "someInputNameInCode": "null"
                      },
                      "output": {
                        "someOutputNameInCode": "null"
                      },
                      "type": "TestNode1"
                    }
                  ],
                  "input": {},
                  "output": {},
                  "type": "SelectorFlow"
                }""");
    }

}