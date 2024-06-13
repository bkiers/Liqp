package liqp.nodes;

import liqp.TemplateContext;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
                List<String> tempResultStrings = new ArrayList<>();
                for (Object obj : list) {
                    tempResultStrings.add(asString(obj, context));
                }
                builder.append("[").append(String.join(", ", tempResultStrings)).append("]");
            } else if (value.getClass().isArray()) {

                Object[] array = (Object[]) value;
                List<String> tempResultStrings = new ArrayList<>();
                for (Object obj : array) {
                    tempResultStrings.add(asString(obj, context));
                }
                builder.append("[").append(String.join(", ", tempResultStrings)).append("]");
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
            return getValueAsString(value, false);
        }
    }

    private String mapToString(Map<Object, Object> map) {
        return map.entrySet().stream()
                .map(entry -> "\"" + entry.getKey() + "\": " + getValueAsString(entry.getValue(), true))
                .collect(Collectors.joining(", ", "{", "}"));
    }

    private String listToString(List<Object> list) {
        return list.stream()
                .map(entry -> getValueAsString(entry, true))
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private String getValueAsString(Object value, boolean mapString) {
        if (Objects.isNull(value)) {
            return "null";
        }
        if (value instanceof Map) {
            return mapToString((Map<Object, Object>) value);
        } else if (value instanceof List || value.getClass().isArray()) {
            return listToString((List<Object>) value);
        } else {
            String stringValue = String.valueOf(value);
            return (mapString) ? getValueWithChecks(value) : stringValue;
        }
    }

    private String getValueWithChecks(Object value) {
        if (Objects.isNull(value)) {
            return "null";
        }
        if (!Objects.isNull(value) && value instanceof String) {
            return "\"" + String.valueOf(value) + "\"";
        }
        else {
            return String.valueOf(value);
        }
    }
}
