package liqp.nodes;

import liqp.TemplateContext;
import liqp.filters.Filter;

import java.util.ArrayList;
import java.util.List;

public class FilterNode implements LNode {

    private Filter filter;
    private List<LNode> params;

    public FilterNode(String filterName, Filter filter) {
        if (filter == null) {
            throw new IllegalArgumentException("no filter available  named: " + filterName);
        }
        this.filter = filter;
        this.params = new ArrayList<LNode>();
    }

    public void add(LNode param) {
        params.add(param);
    }

    public Object apply(Object value, TemplateContext context) {

        List<Object> paramValues = new ArrayList<Object>();

        for (LNode node : params) {
            paramValues.add(node.render(context));
        }

        return filter.apply(value, paramValues.toArray(new Object[paramValues.size()]));
    }

    @Override
    public Object render(TemplateContext context) {
        throw new RuntimeException("cannot render a filter");
    }
}
