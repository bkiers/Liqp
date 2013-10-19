package liqp;

import java.util.Map;

public class Context {

    private final Context parent;
    private Map<String, Object> variables;

    public Context() {
        this(null);
    }

    public Context(Context parent) {
        this.parent = parent;
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

    @Override
    public String toString() {
        return "Context{" +
                "parent=" + parent +
                ", variables=" + variables +
                '}';
    }
}
