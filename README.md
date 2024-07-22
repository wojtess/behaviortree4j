# BehaviorTree

A Java implementation of a behavior tree framework. This project supports encoding and decoding nodes from JSON, enabling flexible behavior tree configurations.

## Overview

Behavior Trees (BTs) are hierarchical models used for decision-making in AI and other areas. In this implementation, nodes return one of three states: `SUCCESS`, `FAILURE`, or `RUNNING`. To define and use nodes, implement the `Node.java` interface.

### Node Types

There are three primary types of nodes to be aware of:

1. **IONode**
2. **Decorator**
3. **FlowController**

#### IONode

`IONode` is a specialized node that can declare inputs and outputs used in JSON configurations. Use the `@Input` and `@Output` annotations to define fields. In your JSON configuration, you can specify `input` and `output` objects that hold names and values of these variables.

#### Decorator

A `Decorator` node has one input and one output. It is used to modify the return value of another node. Currently, the implementation includes:
- `InverterDecorator.java`: Inverts the result of the child node.

#### FlowController

A `FlowController` node manages child nodes and determines the order of execution. There are three implementations:
- `SelectorFlow.java`
- `SequenceFlow.java`
- `SequenceFlowWithMemory.java`

In JSON, you specify the `children` array, which contains the nodes to be executed by the `FlowController`.

## JSON Configuration

Nodes can be decoded from JSON to create behavior trees. The JSON representation starts with a root node and includes definitions for child nodes as needed.

### Example
```json
{
  "children": [
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

## Getting Started

1. **Setup:**
   - Ensure you have [Java](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) and [Maven](https://maven.apache.org/download.cgi) installed.
   - Clone the repository using Git:
     ```bash
     git clone <repository-url>
     ```
   - Navigate to the project directory:
     ```bash
     cd <project-directory>
     ```

2. **Build the Project:**
   - Use Maven to build the project:
     ```bash
     mvn clean install
     ```

3. **Define Nodes:**
   - Implement your custom nodes by extending `IONode`, `Decorator`, or `FlowController`.

4. **Configure JSON:**
   - Create JSON files to define your behavior trees. Use the provided methods to decode and execute them.

For detailed usage and advanced configurations, please refer to the project's documentation and source code.
