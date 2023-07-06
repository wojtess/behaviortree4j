package me.wojtess;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BehaviorContext {

    private Map<String, Object> map = new HashMap<>();
    private BehaviorContext parent = null;

    /**
     * searching in this context and parent context
     * @param key to search in map and in parent
     */
    public Optional<Object> getValue(String key) {
        Optional<Object> value = Optional.ofNullable(map.get(key));
        if(value.isEmpty() && parent != null) {
            value = parent.getValue(key);
        }
        return value;
    }

    /**
     * searching in this context and parent context
     * @param key to search in map and in parent
     * @param clazz to cast
     */
    public <T> Optional<T> getValue(String key, Class<T> clazz) {
        try {
            var value = getValue(key);
            return value.map(clazz::cast);
        } catch (ClassCastException ex) {
            return Optional.empty();
        }
    }

    /**
     * searching **ONLY** in this context
     * @param key to search
     */
    public Optional<Object> getLocalValue(String key) {
        return Optional.ofNullable(map.get(key));
    }

    /**
     * searching **ONLY** in this context
     * @param key to search
     * @param clazz to cast
     */
    public <T> Optional<T> getLocalValue(String key, Class<T> clazz) {
        var value = getLocalValue(key);
        try {
            return value.map(clazz::cast);
        } catch (ClassCastException ex) {
            return Optional.empty();
        }
    }

    /**
     * put value to current context
     * @param key to put
     * @param value to map to key
     * @return previous value with this key
     */
    public Optional<Object> putValue(String key, Object value) {
        return Optional.ofNullable(map.put(key, value));
    }

    /**
     * get value from key and then return it, uses current context
     * @param key for search
     * @return value mapped to this key
     */
    public Optional<Object> getAndRemoveValue(String key) {
        return Optional.ofNullable(map.remove(key));
    }

    /**
     * get value from key and then return it, uses current context
     * @param key for search
     * @return value mapped to this key, returns Optional.empty() when value can`t be cast
     */
    public <T> Optional<T> getAndRemoveValue(String key, Class<T> clazz) {
        var value = getAndRemoveValue(key);
        try {
            return value.map(clazz::cast);
        } catch (ClassCastException ex) {
            value.ifPresent(v -> {
                putValue(key, v);
            });
            return Optional.empty();
        }
    }

    /**
     * create new children from this context
     * @return new children
     */
    public BehaviorContext getChild() {
        var a = new BehaviorContext();
        a.parent = this;
        a.map = new HashMap<>(this.map);
        return a;
    }

}
