package liqp.tags;

import liqp.TemplateContext;
import liqp.nodes.LNode;

public class Break extends Tag {

    @Override
    public Object render(TemplateContext context, LNode... nodes) {
        return BREAK;
    }
}
