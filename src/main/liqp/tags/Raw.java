package liqp.tags;

import liqp.nodes.LNode;

import java.util.Map;

class Raw extends Tag {

    /*
     * temporarily disable tag processing to avoid syntax conflicts.
     */
    @Override
    public Object render(Map<String, Object> context, LNode... nodes) {
        return nodes[0].render(context);
    }
}
