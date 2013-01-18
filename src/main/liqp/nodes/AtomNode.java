package liqp.nodes;

import java.util.Map;

class AtomNode implements LNode {

    private Object value;

    public AtomNode(Object value) {
        this.value = value;
    }

    @Override
    public Object render(Map<String, Object> context) {

        return value;
    }
}
