package liqp.nodes;

import java.util.Map;

public class AtomNode implements LNode {

    private Object value;

    public AtomNode(Object value) {
        this.value = value;
    }

    @Override
    public Object render(Map<String, Object> variables) {

        return value;
    }
}
