package liqp;

import liqp.blocks.BlockDelegate;
import liqp.nodes.LNode;
import liqp.tags.TagDelegate;

public interface InsertionDelegate {

    static Insertion createTag(InsertionDelegate delegate) {
        return new TagDelegate(delegate);
    }

    static Insertion createBlock(InsertionDelegate delegate) {
        return new BlockDelegate(delegate);
    }
    
    /**
     * Define name of this tag.
     */
    String getName();

    /**
     * @see Insertion#render(TemplateContext, LNode...)
     */
    Object render(TemplateContext context, LNode... nodes);
}
