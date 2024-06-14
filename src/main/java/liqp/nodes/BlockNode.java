package liqp.nodes;

import liqp.TemplateContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
import static liqp.constants.Constants.EMPTY_STRING;

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
                List<Object> list = (List<Object>) value;
                builder.append(listToString(list));
            } else if (value.getClass().isArray()) {
                Object[] array = (Object[]) value;
                builder.append(listToString(List.of(array)));
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
        try {
            JSONObject jsonObject = new JSONObject(map);
            return jsonObject.toJSONString();
        }
        catch (Exception exception) {
            System.err.println("Exception occurred converting map to a JSONObject. Returning empty string: " + exception.getMessage());
            return EMPTY_STRING;
        }
    }

    private String listToString(List<Object> list) {
        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray.addAll(list);
            return jsonArray.toJSONString();
        }
        catch (Exception exception) {
            System.err.println("Exception occurred converting list to a JSONObject. Returning empty string: " + exception.getMessage());
            return EMPTY_STRING;
        }
    }

    private String getValueAsString(Object value) {
        try {
            if (Objects.isNull(value)) {
                return "null";
            }
            if (value instanceof Map) {
                return mapToString((Map<Object, Object>) value);
            } else if (value instanceof List || value.getClass().isArray()) {
                return listToString((List<Object>) value);
            } else {
                return String.valueOf(value);
            }
        }
        catch (Exception exception) {
            System.err.println("Exception occurred converting value to a string. Returning empty string: " + exception.getMessage());
            return EMPTY_STRING;
        }
    }
}
