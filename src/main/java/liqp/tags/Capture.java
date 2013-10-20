package liqp.tags;

import liqp.Context;
import liqp.nodes.LNode;

class Capture extends Tag {

    /*
     * Block tag that captures text into a variable
     */
    @Override
    public Object render(Context context, LNode... nodes) {

        String id = super.asString(nodes[0].render(context));

        LNode block = nodes[1];

        context.put(id, block.render(context));

        return null;
    }
}
