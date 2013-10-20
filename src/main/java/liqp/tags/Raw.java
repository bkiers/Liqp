package liqp.tags;

import liqp.Context;
import liqp.nodes.LNode;

class Raw extends Tag {

    /*
     * temporarily disable tag processing to avoid syntax conflicts.
     */
    @Override
    public Object render(Context context, LNode... nodes) {
        return nodes[0].render(context);
    }
}
