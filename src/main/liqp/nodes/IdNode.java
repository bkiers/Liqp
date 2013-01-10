package liqp.nodes;

import java.util.Map;

public class IdNode implements LNode {

    private String id;

    public IdNode(String id) {
        this.id = id;
    }

    @Override
    public Object render(Map<String, Object> variables) {
        return id;
    }
}
