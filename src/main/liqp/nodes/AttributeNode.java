package liqp.nodes;

import java.util.Map;

public class AttributeNode implements LNode {

    private LNode key;
    private LNode value;

    public AttributeNode(LNode key, LNode value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public Object render(Map<String, Object> variables) {

        return new Object[]{
                key.render(variables),
                value.render(variables)
        };
    }
}
