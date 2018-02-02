package liqp.nodes;

import liqp.TemplateContext;
import liqp.exceptions.LiquidException;
import liqp.filters.Filter;
import org.antlr.runtime.tree.CommonTree;

import java.util.ArrayList;
import java.util.List;

public class FilterNode implements LNode {

    private final Filter filter;
    private final List<LNode> params;
    private final CommonTree tree;

    public FilterNode(CommonTree tree, Filter filter) {
        if (filter == null) {
            throw new IllegalArgumentException("error on line " + tree.getLine() + ", index " +
                tree.getTokenStartIndex() + ": no filter available named: " + tree.getText());
        }
        this.filter = filter;
        this.params = new ArrayList<LNode>();
        this.tree = tree;
    }

    public void add(LNode param) {
        params.add(param);
    }

    public Object apply(Object value, TemplateContext context) {

        try {
            List<Object> paramValues = new ArrayList<Object>();

            for (LNode node : params) {
                paramValues.add(node.render(context));
            }

            return filter.apply(value, context, paramValues.toArray(new Object[paramValues.size()]));
        }
        catch (Exception e) {
            throw new RuntimeException("error on line " + tree.getLine() + ", index " +
                tree.getTokenStartIndex() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Object render(TemplateContext context) {
        throw new RuntimeException("cannot render a filter");
    }
}
