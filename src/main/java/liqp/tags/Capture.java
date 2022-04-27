package liqp.tags;

import liqp.TemplateContext;
import liqp.nodes.LNode;

class Capture extends Block {

    /*
     * Block tag that captures text into a variable
     */
    @Override
    public Object render(TemplateContext context, LNode... nodes) {

        String id = super.asString(nodes[0].render(context), context);

        LNode block = nodes[1];

        // Capture causes variable to be saved "globally"
        context.put(id, block.render(context), true);

        return null;
    }
}
