package liqp.nodes;

import java.util.Map;

public class AtomNode implements LNode {

    private Object value;

    public AtomNode(Object value) {
        this.value = value;
    }

    @Override
    public Object render(Map<String, Object> variables) {

        if(value instanceof Number) {

            double d = ((Number)value).doubleValue();

            if(d == (long)d) {
                return (long)d;
            }
        }

        return value;
    }
}
