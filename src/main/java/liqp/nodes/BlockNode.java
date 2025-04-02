package liqp.nodes;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import liqp.RenderTransformer.ObjectAppender;
import liqp.TemplateContext;

import static liqp.LValue.*;

public class BlockNode implements LNode {

    private List<LNode> children;

    public BlockNode() {
        this(false);
    }

    public BlockNode(boolean isRootBlock) {
        this.children = new ArrayList<LNode>();
    }

    public void add(LNode node) {
        children.add(node);
    }

    public List<LNode> getChildren() {
        return new ArrayList<LNode>(children);
    }

    @Override
    public Object render(TemplateContext context) {
        ObjectAppender.Controller builder = context.newObjectAppender(children.size());
        for (LNode node : children) {

            // Since tags can be "empty", `node` can be null, in which case we simply continue.
            // For example, the tag `{% # inline comment %}` is considered "empty" since there is nothing inside.
            if (node == null) {
                continue;
            }

            Object value = node.render(context);
            if (value == null) {
                continue;
            }

            if (value == BREAK || value == CONTINUE) {
                return value;
            } else if (value instanceof List) {

                List<?> list = (List<?>) value;

                for (Object obj : list) {
                    builder.append(postprocess(obj, context));
                }
            } else if (value.getClass().isArray()) {

                Object[] array = (Object[]) value;
                for (Object obj : array) {
                    builder.append(postprocess(obj, context));
                }
            } else {
                builder.append(postprocess(value, context));
            }
        }

        return builder.getResult();
    }

    private Object postprocess(Object value, TemplateContext context) {
        if (isTemporal(value)) {
            ZonedDateTime time = asRubyDate(value, context);
            return rubyDateTimeFormat.format(time);
        } else {
            return value;
        }
    }
}
