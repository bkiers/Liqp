package liqp.blocks;

import liqp.TemplateContext;
import liqp.nodes.LNode;

public class Raw extends Block {

    /*
     * temporarily disable tag processing to avoid syntax conflicts.
     */
    @Override
    public Object render(TemplateContext context, LNode... nodes) {
        return nodes[0].render(context);
    }
}
