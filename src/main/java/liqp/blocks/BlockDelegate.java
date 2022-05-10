package liqp.blocks;

import liqp.Insertion;
import liqp.InsertionDelegate;
import liqp.TemplateContext;
import liqp.nodes.LNode;
import liqp.tags.Tag;

public class BlockDelegate extends Tag {

    public static Insertion getBlock(InsertionDelegate delegate) {
        return new BlockDelegate(delegate);
    }

    private final InsertionDelegate delegate;

    public BlockDelegate(InsertionDelegate delegate) {
        super(delegate.getName());
        this.delegate = delegate;
    }

    @Override
    public Object render(TemplateContext context, LNode... nodes) {
        return delegate.render(context, nodes);
    }
}