package liqp.nodes;

import liqp.TemplateContext;
import liqp.filters.Filter;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.List;

public class FilterNode implements LNode {

    private final int line;
    private final int tokenStartIndex;
    private final String text;
    private final Filter filter;
    private final List<LNode> params;

    public FilterNode(ParserRuleContext context, Filter filter) {
        this(context.start.getLine(), context.start.getCharPositionInLine(), context.getText(), filter);
    }

    private FilterNode(int line, int tokenStartIndex, String text, Filter filter) {

        if (filter == null) {
            throw new IllegalArgumentException("error on line " + line + ", index " + tokenStartIndex + ": no filter available named: " + text);
        }

        this.line = line;
        this.tokenStartIndex = tokenStartIndex;
        this.text = text;
        this.filter = filter;
        this.params = new ArrayList<LNode>();
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
            throw new RuntimeException("error on line " + line + ", index " + tokenStartIndex + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Object render(TemplateContext context) {
        throw new RuntimeException("cannot render a filter");
    }
}
