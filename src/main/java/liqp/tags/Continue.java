package liqp.tags;

import liqp.TemplateContext;
import liqp.nodes.LNode;

public class Continue extends Tag {

    @Override
    public Object render(TemplateContext context, LNode... nodes) {
        return CONTINUE;
    }
}
