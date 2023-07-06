package me.wojtess;


import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class IONode implements Node {

    /**
     * Used for mapping input and output values to context values
     */
    private final Map<Field, String> inputToContext;
    private final Map<Field, String> outputToContext;

    public IONode() {
        inputToContext = new HashMap<>();
        outputToContext = new HashMap<>();
    }

    protected Map<Field, String> getInputToContext() {
        return inputToContext;
    }

    //for decoding
    protected void putInput(String key, String value) throws NoSuchFieldException {
        var field = getClass().getDeclaredField(key);
        field.setAccessible(true);
        inputToContext.put(field, value);
    }

    //for decoding
    protected void putOutput(String key, String value) throws NoSuchFieldException {
        var field = getClass().getDeclaredField(key);
        field.setAccessible(true);
        outputToContext.put(field, value);
    }

    protected Map<Field, String> getOutputToContext() {
        return outputToContext;
    }

    @Override
    public Status tick(BehaviorContext context) throws Throwable {
        for (Map.Entry<Field, String> fieldStringEntry : inputToContext.entrySet()) {
            try {
                Field f = fieldStringEntry.getKey();
                String fieldName = fieldStringEntry.getValue();
                Optional<?> value = Optional.empty();

                //check if field is variable
                if(fieldName.startsWith("{") && fieldName.endsWith("}")) {
                    //get value of variable from context
                    value = context.getValue(fieldName, f.getType());
                } else {
                    //get value from saved data from json
                    if(f.getType().equals(String.class)) {
                        //string
                        value = Optional.of(fieldName);
                    } else {
                        //numbers
                        try {
                            //get method that converts string to value, ex: Integer.valueOf("12") or Double.valueOf("12.213");
                            Method method = ClassUtils.primitiveToWrapper(f.getType()).getMethod("valueOf", String.class);
                            if (Modifier.isStatic(method.getModifiers())) {
                                value = Optional.ofNullable(method.invoke(null, fieldName));
                            }
                        } catch (InvocationTargetException | NoSuchMethodException ex) {
                            //this can happen so we will just ignore it
                            //this probably wont be in docs, so anyone who is trying to pass
                            //something else that number or string via json will have punishment
                            //for hoping that I am not lazy
                        }
                    }
                }
                if(value.isPresent()) {
                    f.set(this, value.get());
                }
            } catch (IllegalAccessException e) {
                //this can happen when node is in another JVM module I think.
                //or some other reflection code changed accessible of field(see line 34 and 41)
                throw e;
            }
        }
        Status returnValue;
        try {
            returnValue = abstractTick(context);
        } catch (Throwable e) {
            returnValue = Status.FAILURE;
        }
        for (Map.Entry<Field, String> fieldStringEntry : outputToContext.entrySet()) {
            try {
                Field f = fieldStringEntry.getKey();
                Object value = f.get(this);
                context.putValue(fieldStringEntry.getValue(), value);
            } catch (IllegalAccessException e) {
                //this can happen when node is in another JVM module I think.
                //or some other reflection code changed accessible of field(see line 34 and 41)
                throw e;
            }
        }
        return returnValue;
    }

    public abstract Status abstractTick(BehaviorContext context) throws Throwable;
}
