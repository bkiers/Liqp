package liqp.tags;

import liqp.TemplateContext;
import liqp.nodes.FilterNode;
import liqp.nodes.LNode;

class Assign extends Tag {

    /*
     * Assigns some value to a variable
     */
    @Override
    public Object render(TemplateContext context, LNode... nodes) {

        String id = String.valueOf(nodes[0].render(context));
        LNode expression = nodes[1];
        Object value = expression.render(context);

        for (int i = 2; i < nodes.length; i++) {
            FilterNode filter = (FilterNode)nodes[i];
            value = filter.apply(value, context);
        }

        // Assign causes variable to be saved "globally"
        context.put(id, value, true);

        return "";
    }
}
