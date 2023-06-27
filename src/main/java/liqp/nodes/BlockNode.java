package liqp.nodes;

import static liqp.LValue.BREAK;
import static liqp.LValue.CONTINUE;
import static liqp.LValue.asTemporal;
import static liqp.LValue.isTemporal;
import static liqp.LValue.rubyDateTimeFormat;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import liqp.RenderTransformer.ObjectAppender;
import liqp.TemplateContext;

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
            ZonedDateTime time = asTemporal(value, context);
            return rubyDateTimeFormat.format(time);
        } else {
            return value;
        }
    }
}
