package liqp.tags;

import liqp.Insertion;
import liqp.InsertionDelegate;
import liqp.TemplateContext;
import liqp.nodes.LNode;

public class TagDelegate extends Tag {

    private final InsertionDelegate delegate;

    public TagDelegate(InsertionDelegate delegate) {
        super(delegate.getName());
        this.delegate = delegate;
    }

    @Override
    public Object render(TemplateContext context, LNode... nodes) {
        return delegate.render(context, nodes);
    }
}
