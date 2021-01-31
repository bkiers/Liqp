package liqp.nodes;

import liqp.TemplateContext;
import liqp.spi.BasicTypesSupport;

import java.util.ArrayList;
import java.util.List;

public class OutputNode implements LNode {

    private LNode expression;
    private List<FilterNode> filters;

    public OutputNode(LNode expression) {
        this.expression = expression;
        this.filters = new ArrayList<FilterNode>();
    }

    public void addFilter(FilterNode filter) {
        filters.add(filter);
    }

    @Override
    public Object render(TemplateContext context) {

        Object value = expression.render(context);

        value = BasicTypesSupport.restoreObject(context, value);

        for (FilterNode node : filters) {
            value = node.apply(value, context);
        }

        return value;
    }
}
