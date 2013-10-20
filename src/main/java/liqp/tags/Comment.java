package liqp.tags;

import liqp.Context;
import liqp.nodes.LNode;

class Comment extends Tag {

    /*
     * Block tag, comments out the text in the block
     */
    @Override
    public Object render(Context context, LNode... nodes) {
        return "";
    }
}
