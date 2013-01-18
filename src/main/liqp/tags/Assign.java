package liqp.tags;

import liqp.nodes.LNode;

import java.util.Map;

class Assign extends Tag {

    /*
     * Assigns some value to a variable
     */
    @Override
    public Object render(Map<String, Object> context, LNode... nodes) {

        String id = String.valueOf(nodes[0].render(context));
        LNode expression = nodes[1];

        Object value = expression.render(context);

        context.put(id, value);

        return "";
    }
}
