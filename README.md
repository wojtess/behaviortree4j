# behaviortree

behavior tree implementation in java.
It can decode encode and decode nodes from json.

# Nodes
Node can return SUCCESS, FAILURE and RUNNING. To implement node you need to implement Node.java, there is 3 important implementations. First is IONode.java, second is Decorator.java, and last is FlowController.class, all of them are abstract.
### IONode
IONode is special node that can declare inputs and outputs that can be used in json. Use @Input and @Output annotations when declaring fields. In json you can specify additional `output` and `input` object that holds names and values of variables.
### Decorator
Decorator is node that have one input and one output, can be used for changing return value of node. Currentl there is one implementation of it. InverterDecorator.java
### FlowController
FlowController is node that have childrens. FlowController have three implementations. SelectorFlow.java, SequenceFlow.java and SequenceFlowWithMemory.java. In json you can specify additional `childrens` array that holds nodes that will be executed.


# Json
Nodes can be decoded from json. Json is staring from one node. Use FlowControllers and other Nodes to create behavior trees.
#### Example:
```
{
  "childrens": [
    {
      "output": {
        "outputValue": "{some_value}"
      },
      "type": "TestNode3"
    },
    {
      "input": {
        "someInput": "{some_value}",
        "nickname": "wojtess"
      },
      "type": "TestNode4"
    }
  ],
  "input": {},
  "output": {},
  "type": "SequenceFlow"
}
```
In example above there is declaration of SequenceFlow with two children of TestNode3 and TestNode4, this nodes are dummy nodes and don't do any important work(see EncoderDecoderBehaviorTreeTest.java). TestNode3 in java code have declared varible outputValue of String, TestNode4 have declared someInput of same type, beacuse they are extending IONode they can use @Input and @Output adnotations.
