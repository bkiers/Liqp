package liqp.tags;

import liqp.TemplateContext;
import liqp.nodes.LNode;

class Comment extends Block {

    /*
     * Block tag, comments out the text in the block
     */
    @Override
    public Object render(TemplateContext context, LNode... nodes) {
        return "";
    }
}
