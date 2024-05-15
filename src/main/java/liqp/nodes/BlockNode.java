package liqp.nodes;

import liqp.TemplateContext;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static liqp.LValue.BREAK;
import static liqp.LValue.CONTINUE;
import static liqp.LValue.asTemporal;
import static liqp.LValue.isTemporal;
import static liqp.LValue.rubyDateTimeFormat;

public class BlockNode implements LNode {

    private List<LNode> children;
    private final boolean isRootBlock;

    public BlockNode() {
        this(false);
    }

    public BlockNode(boolean isRootBlock) {
        this.children = new ArrayList<LNode>();
        this.isRootBlock = isRootBlock;
    }

    public void add(LNode node) {
        children.add(node);
    }

    public List<LNode> getChildren() {
        return new ArrayList<LNode>(children);
    }

    @Override
    public Object render(TemplateContext context) {

        StringBuilder builder = new StringBuilder();

        for (LNode node : children) {

            Object value = node.render(context);

            if(value == null) {
                continue;
            }

            if(value == BREAK || value == CONTINUE) {
                return value;
            } else if (value instanceof List) {

                List<?> list = (List<?>) value;

                for (Object obj : list) {
                    builder.append(asString(obj, context));
                }
            } else if (value.getClass().isArray()) {

                Object[] array = (Object[]) value;
                for (Object obj : array) {
                    builder.append(asString(obj, context));
                }
            } else {
                builder.append(asString(value, context));
            }

            if (builder.length() > context.protectionSettings.maxSizeRenderedString) {
                throw new RuntimeException("rendered string exceeds " + context.protectionSettings.maxSizeRenderedString);
            }
        }

        return builder.toString();
    }

    private String asString(Object value, TemplateContext context) {
        if (isTemporal(value)) {
            ZonedDateTime time = asTemporal(value, context);
            return rubyDateTimeFormat.format(time);
        } else {
            return getValueAsString(value);
        }
    }

    private String mapToString(Map<Object, Object> map) {
        return map.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + getValueAsString(entry.getValue()))
                .collect(Collectors.joining(", ", "{", "}"));
    }

    private String getValueAsString(Object value) {
        if (value instanceof Map) {
            return mapToString((Map<Object, Object>) value);
        } else {
            return String.valueOf(value);
        }
    }
}
