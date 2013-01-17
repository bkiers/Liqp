package liqp.tags;

import liqp.nodes.LNode;

import java.util.Map;

class Raw extends Tag {

    /*
     * temporarily disable tag processing to avoid syntax conflicts.
     */
    @Override
    public Object render(Map<String, Object> variables, LNode... tokens) {
        return tokens[0].render(variables);
    }
}
