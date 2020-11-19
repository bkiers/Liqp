package liqp.tags;

import java.util.ArrayList;
import java.util.List;

import liqp.LValue;
import liqp.TemplateContext;
import liqp.exceptions.ExceededMaxIterationsException;
import liqp.nodes.BlockNode;
import liqp.nodes.LNode;

import java.util.HashMap;
import java.util.Map;

class For extends Tag {

    private static final String OFFSET = "offset";
    private static final String LIMIT = "limit";
    private static final String CONTINUE = "continue";

    /*
     * forloop.length      # => length of the entire for loop
     * forloop.index       # => index of the current iteration
     * forloop.index0      # => index of the current iteration (zero based)
     * forloop.rindex      # => how many items are still left?
     * forloop.rindex0     # => how many items are still left? (zero based)
     * forloop.first       # => is this the first iteration?
     * forloop.last        # => is this the last iteration?
     */
    static final String FORLOOP = "forloop";
    static final String LENGTH = "length";
    static final String INDEX = "index";
    static final String INDEX0 = "index0";
    static final String RINDEX = "rindex";
    static final String RINDEX0 = "rindex0";
    static final String FIRST = "first";
    static final String LAST = "last";
    static final String NAME = "name";

    /*
     * For loop
     */
    @Override
    public Object render(TemplateContext context, LNode... nodes) {

        // The first node in the array denotes whether this is a for-tag
        // over an array, `for item in array ...`, or a for-tag over a
        // range, `for i in (4..item.length)`.
        boolean array = super.asBoolean(nodes[0].render(context));

        String id = super.asString(nodes[1].render(context));

        // Each for tag has its own context that keeps track of its own variables (scope)
        TemplateContext nestedContext = new TemplateContext(context);

        nestedContext.put(FORLOOP, new HashMap<String, Object>());

        Object rendered;
        if (array) {
            rendered = renderArray(id, nestedContext, nodes);
        } else {
            rendered = renderRange(id, nestedContext, nodes);
        }

        return rendered;
    }

    private Object renderArray(String id, TemplateContext context, LNode... tokens) {

        StringBuilder builder = new StringBuilder();

        // attributes start from index 5
        Map<String, Integer> attributes = getAttributes(5, context, tokens);

        int offset = attributes.get(OFFSET);
        int limit = attributes.get(LIMIT);

        Object data = tokens[2].render(context);
        if (data instanceof Map) {
            data = mapAsArray((Map) data);
        }
        Object[] array = super.asArray(data);

        LNode block = tokens[3];
        LNode blockIfEmptyOrNull = tokens[4];

        if(array == null || array.length == 0) {
            return blockIfEmptyOrNull == null ? null : blockIfEmptyOrNull.render(context);
        }

        int length = Math.min(limit, array.length);

        Map<String, Object> forLoopMap = (Map<String, Object>)context.get(FORLOOP);

        forLoopMap.put(NAME, id + "-" + tokens[2].toString());

        int continueIndex = offset;

        for (int i = offset, n = 0; n < limit && i < array.length; i++, n++) {

            context.incrementIterations();

            continueIndex = i;

            boolean first = (i == offset);
            boolean last = ((n == limit - 1) || (i == array.length - 1));

            context.put(id, array[i]);

            forLoopMap.put(LENGTH, length);
            forLoopMap.put(INDEX, n + 1);
            forLoopMap.put(INDEX0, n);
            forLoopMap.put(RINDEX, length - n);
            forLoopMap.put(RINDEX0, length - n - 1);
            forLoopMap.put(FIRST, first);
            forLoopMap.put(LAST, last);

            List<LNode> children = ((BlockNode)block).getChildren();
            boolean isBreak = false;

            for (LNode node : children) {

                Object value = node.render(context);

                if(value == LValue.CONTINUE) {
                    // break from this inner loop: equals continue outer loop!
                    break;
                }

                if(value == LValue.BREAK) {
                    // break from inner loop
                    isBreak = true;
                    break;
                }

                if (value != null && value.getClass().isArray()) {

                    Object[] arr = (Object[]) value;

                    for (Object obj : arr) {
                        builder.append(String.valueOf(obj));
                    }
                }
                else {
                    builder.append(super.asString(value));
                }
            }

            if(isBreak) {
                // break from outer loop
                break;
            }
        }

        context.put(CONTINUE, continueIndex + 1, true);

        return builder.toString();
    }

    private Object renderRange(String id, TemplateContext context, LNode... tokens) {

        StringBuilder builder = new StringBuilder();

        // attributes start from index 5
        Map<String, Integer> attributes = getAttributes(5, context, tokens);

        int offset = attributes.get(OFFSET);
        int limit = attributes.get(LIMIT);

        LNode block = tokens[4];

        try {
            int from = super.asNumber(tokens[2].render(context)).intValue();
            int to = super.asNumber(tokens[3].render(context)).intValue();

            int length = (to - from);

            Map<String, Object> forLoopMap = (Map<String, Object>)context.get(FORLOOP);

            int continueIndex = from + offset;

            for (int i = from + offset, n = 0; i <= to && n < limit; i++, n++) {

                context.incrementIterations();

                continueIndex = i;

                boolean first = (i == (from + offset));
                boolean last = ((i == to) || (n == limit - 1));

                context.put(id, i);

                forLoopMap.put(LENGTH, length);
                forLoopMap.put(INDEX, n + 1);
                forLoopMap.put(INDEX0, n);
                forLoopMap.put(RINDEX, length - n);
                forLoopMap.put(RINDEX0, length - n - 1);
                forLoopMap.put(FIRST, first);
                forLoopMap.put(LAST, last);

                List<LNode> children = ((BlockNode)block).getChildren();
                boolean isBreak = false;

                for (LNode node : children) {

                    Object value = node.render(context);

                    if(value == null) {
                        continue;
                    }

                    if(value == LValue.CONTINUE) {
                        // break from this inner loop: equals continue outer loop!
                        break;
                    }

                    if(value == LValue.BREAK) {
                        // break from inner loop
                        isBreak = true;
                        break;
                    }

                    if(super.isArray(value)) {

                        Object[] arr = super.asArray(value);

                        for (Object obj : arr) {
                            builder.append(String.valueOf(obj));
                        }
                    }
                    else {
                        builder.append(super.asString(value));
                    }
                }

                if(isBreak) {
                    // break from outer loop
                    break;
                }
            }

            context.put(CONTINUE, continueIndex + 1);
        }
        catch (ExceededMaxIterationsException e) {
            throw e;
        }
        catch (Exception e) {
            /* just ignore incorrect expressions */
        }

        return builder.toString();
    }

    private Map<String, Integer> getAttributes(int fromIndex, TemplateContext context, LNode... tokens) {

        Map<String, Integer> attributes = new HashMap<String, Integer>();

        attributes.put(OFFSET, 0);
        attributes.put(LIMIT, Integer.MAX_VALUE);

        for (int i = fromIndex; i < tokens.length; i++) {

            Object[] attribute = super.asArray(tokens[i].render(context));

            try {
                attributes.put(super.asString(attribute[0]), super.asNumber(attribute[1]).intValue());
            }
            catch (Exception e) {
                /* just ignore incorrect attributes */
            }
        }

        return attributes;
    }
}
