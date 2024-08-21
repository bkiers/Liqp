package liqp.nodes;

import liqp.TemplateContext;

import java.util.ArrayList;
import java.util.List;

public class OutputNode implements LNode {

    private LNode expression;
    private List<FilterNode> filters;
    private final String EMPTY_STRING = "";

    public OutputNode(LNode expression) {
        this.expression = expression;
        this.filters = new ArrayList<FilterNode>();
    }

    public void addFilter(FilterNode filter) {
        filters.add(filter);
    }

    @Override
    public Object render(TemplateContext context) {
        try {
            Object value = expression.render(context);

            for (FilterNode node : filters) {
                value = node.apply(value, context);
            }

            return value;
        } catch (Exception ex) {
            return EMPTY_STRING;
        }
    }
}
