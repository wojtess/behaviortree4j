package me.wojtess;

import com.google.gson.*;
import me.wojtess.impl.flow.FlowController;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class EncoderDecoderBehaviorTree {

    private static final Collection<Class<? extends Node>> defaultNodes;

    static {
        Reflections reflections = new Reflections("me.wojtess");
        defaultNodes = reflections.getSubTypesOf(Node.class);
    }

    public static Node decode(String data, Collection<Class<? extends Node>> nodes) throws UnknownTypeException, WrongConstructorException {
        Set<Class<? extends Node>> setNodes = new HashSet<>(Set.copyOf(nodes));
        setNodes.addAll(defaultNodes);
        JsonElement rootElement = JsonParser.parseString(data);
        return decodeNode(rootElement.getAsJsonObject(), setNodes);
    }

    private static Node decodeNode(JsonObject root, Collection<Class<? extends Node>> nodes) throws UnknownTypeException, WrongConstructorException {
        String typeName = root.get("type").getAsString();
        Node out = null;
        for (Class<? extends Node> node : nodes) {
            if(node.getSimpleName().equals(typeName)) {
                try {
                    out = node.getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new WrongConstructorException(typeName);
                }
                break;
            }
        }
        if(out == null) {
            throw new UnknownTypeException(typeName);
        }
        if(out instanceof IONode ioNode) {
            if(root.has("output")) {
                for (Map.Entry<String, JsonElement> stringJsonElementEntry : root.getAsJsonObject("output").entrySet()) {
                    String value = stringJsonElementEntry.getValue().getAsString();
                    if (!value.equals("null")) {
                        try {
                            ioNode.putOutput(stringJsonElementEntry.getKey(), value);
                        } catch (NoSuchFieldException ex) {
                            ex.printStackTrace();//imposible I think
                        }
                    }
                }
            }
            if(root.has("input")) {
                for (Map.Entry<String, JsonElement> stringJsonElementEntry : root.getAsJsonObject("input").entrySet()) {
                    String value = stringJsonElementEntry.getValue().getAsString();
                    if (!value.equals("null")) {
                        try {
                            ioNode.putInput(stringJsonElementEntry.getKey(), value);
                        } catch (NoSuchFieldException ex) {
                            ex.printStackTrace();//imposible I think
                        }
                    }
                }
            }
        }
        if(out instanceof FlowController flowController) {
            for (JsonElement childrens : root.getAsJsonArray("childrens")) {
                flowController.addChildren(decodeNode(childrens.getAsJsonObject(), nodes));
            }
        }
        return out;
    }

    public static class WrongConstructorException extends Exception {

        private final String name;

        public WrongConstructorException(String name) {
            super(String.format("%s dont have public constructor without any arguments", name));
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class UnknownTypeException extends Exception {
        private final String type;

        public UnknownTypeException(String type) {
            super("Unknown type: " + type);
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    public static String encode(Node rootNode) {
        Map<String, Object> rootMap = encodeNode(rootNode);
        return new GsonBuilder().setPrettyPrinting().create().toJson(rootMap);
    }

    private static Map<String, Object> encodeNode(Node node) {
        Map<String, Object> out = new TreeMap<>();

        if(node.getClass().getSimpleName().length() == 0) {
            if(node.getClass().getSuperclass() != Object.class) {
                out.put("type", node.getClass().getSuperclass().getSimpleName());
            } else {
                out.put("type", node.getClass().getInterfaces()[0].getSimpleName());
            }
        } else {
            out.put("type", node.getClass().getSimpleName());
        }

        if(node instanceof IONode rootNodeIO) {
            Map<String, String> inputs = new TreeMap<>();
            Map<String, String> outputs = new TreeMap<>();
            for (Field declaredField : rootNodeIO.getClass().getDeclaredFields()) {
                String name = declaredField.getName();
                if(declaredField.getAnnotation(Input.class) != null) {
                    String mappedName = rootNodeIO.getInputToContext().get(declaredField);
                    if(mappedName == null) {
                        mappedName = "null";
                    }
                    inputs.put(name, mappedName);
                }
                if(declaredField.getAnnotation(Output.class) != null) {
                    String mappedName = rootNodeIO.getOutputToContext().get(declaredField);
                    if(mappedName == null) {
                        mappedName = "null";
                    }
                    outputs.put(name, mappedName);
                }
            }
            out.put("input", inputs);
            out.put("output", outputs);
        }

        if(node instanceof FlowController flowControllerNode) {
            List<Map<String, Object>> childrens = new ArrayList<>();
            for (Node children : flowControllerNode.getChildrens()) {
                childrens.add(encodeNode(children));
            }
            out.put("childrens", childrens);
        }
        return out;
    }

}
