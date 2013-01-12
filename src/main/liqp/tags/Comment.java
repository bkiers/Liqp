package liqp.tags;

import liqp.nodes.LNode;

import java.util.Map;

public class Comment extends Tag {

    @Override
    public Object render(Map<String, Object> variables, LNode... tokens) {
        return "";
    }
}
