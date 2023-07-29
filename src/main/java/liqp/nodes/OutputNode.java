package liqp.nodes;

import liqp.TemplateContext;
import liqp.TemplateParser;
import org.jsoup.internal.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class OutputNode implements LNode {

    private LNode expression;
    private String unparsed;
    private Integer unparsedPosition;
    private List<FilterNode> filters;

    public OutputNode(LNode expression, String unparsed, Integer unparsedPosition) {
        this.expression = expression;
        this.unparsed = unparsed;
        this.unparsedPosition = unparsedPosition;
        this.filters = new ArrayList<>();
    }

    public void addFilter(FilterNode filter) {
        filters.add(filter);
    }

    @Override
    public Object render(TemplateContext context) {

        Object value = expression.render(context);

        for (FilterNode node : filters) {
            value = node.apply(value, context);
        }
        if (context != null && context.getParser().errorMode == TemplateParser.ErrorMode.WARN) {
            String localUnparsed = unparsed;
            if (!StringUtil.isBlank(localUnparsed)) {
                if (localUnparsed.length() > 30) {
                    localUnparsed = localUnparsed.substring(0, 30) + "...";
                }
                context.addError(new RuntimeException("unexpected output: " + localUnparsed + " at position " + unparsedPosition));
            }

        }

        return value;
    }
}
