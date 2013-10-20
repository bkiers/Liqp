package liqp;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Context {

    private final Context parent;
    private final Map<String, Object> variables;

    public Context() {
        this.parent = null;
        this.variables = new LinkedHashMap<>();
    }

    public Context(Context parent) {
        this.parent = parent;
        this.variables = new LinkedHashMap<>();
    }

    public Context(Map<String, Object> map) {
        this.parent = null;
        this.variables = new LinkedHashMap<>();
        this.variables.putAll(map);
    }

    public boolean containsKey(String identifier) {
        return this.variables.containsKey(identifier);
    }

    public Object get(String identifier) {

        Object value = variables.get(identifier);

        if(value == null && parent != null) {
            return parent.get(identifier);
        }
        else {
            return value;
        }
    }

    public Object put(String identifier, Object value) {
        return variables.put(identifier, value);
    }

    public Object remove(String identifier) {
        return variables.remove(identifier);
    }

    public int size() {
        return variables.size();
    }

    public Collection<Object> values() {
        return variables.values();
    }

    @Override
    public String toString() {
        return "Context{" +
                "parent=" + parent +
                ", variables=" + variables +
                '}';
    }
}
