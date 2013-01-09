package liqp.nodes;

import java.util.Map;

public class PlainNode implements LNode {

    private String text;

    public PlainNode(String text) {
        this.text = text;
    }

    @Override
    public Object render(Map<String, Object> variables) {
        return text;
    }
}
