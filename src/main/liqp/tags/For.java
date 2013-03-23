package liqp.tags;

import liqp.nodes.LNode;

import java.util.HashMap;
import java.util.Map;

class For extends Tag {

    private static final String OFFSET = "offset";
    private static final String LIMIT = "limit";

    /*
     * forloop.length      # => length of the entire for loop
     * forloop.index       # => index of the current iteration
     * forloop.index0      # => index of the current iteration (zero based)
     * forloop.rindex      # => how many items are still left?
     * forloop.rindex0     # => how many items are still left? (zero based)
     * forloop.first       # => is this the first iteration?
     * forloop.last        # => is this the last iteration?
     */
    private static final String FORLOOP = "forloop";
    private static final String LENGTH = "length";
    private static final String INDEX = "index";
    private static final String INDEX0 = "index0";
    private static final String RINDEX = "rindex";
    private static final String RINDEX0 = "rindex0";
    private static final String FIRST = "first";
    private static final String LAST = "last";

    /*
     * For loop
     */
    @Override
    public Object render(Map<String, Object> context, LNode... nodes) {

        // The first node in the array denotes whether this is a for-tag
        // over an array, `for item in array ...`, or a for-tag over a
        // range, `for i in (4..item.length)`.
        boolean array = super.asBoolean(nodes[0].render(context));

        String id = super.asString(nodes[1].render(context));

        context.put(FORLOOP, new HashMap<String, Object>());

        Object rendered = array ? renderArray(id, context, nodes) : renderRange(id, context, nodes);

        context.remove(FORLOOP);

        return rendered;
    }

    private Object renderArray(String id, Map<String, Object> context, LNode... tokens) {

        StringBuilder builder = new StringBuilder();

        // attributes start from index 4
        Map<String, Integer> attributes = getAttributes(4, context, tokens);

        int offset = attributes.get(OFFSET);
        int limit = attributes.get(LIMIT);

        Object[] array = super.asArray(tokens[2].render(context));

        if(array == null) {
            return null;
        }

        LNode block = tokens[3];

        int length = Math.min(limit, array.length);

        Map<String, Object> forLoopMap = (Map<String, Object>)context.get(FORLOOP);

        for (int i = offset, n = 0; n < limit && i < array.length; i++, n++) {

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

            Object renderedBlock = block.render(context);

            if(renderedBlock == Statement.BREAK) {
                break;
            }

            if(renderedBlock == Statement.CONTINUE) {
                continue;
            }

            builder.append(super.asString(renderedBlock));
        }

        return builder.toString();
    }

    private Object renderRange(String id, Map<String, Object> context, LNode... tokens) {

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

            for (int i = from + offset, n = 0; i <= to && n < limit; i++, n++) {

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

                Object renderedBlock = block.render(context);

                if(renderedBlock == Statement.BREAK) {
                    break;
                }

                if(renderedBlock == Statement.CONTINUE) {
                    continue;
                }

                builder.append(super.asString(renderedBlock));
            }
        }
        catch (Exception e) {
            /* just ignore incorrect expressions */
        }

        return builder.toString();
    }

    private Map<String, Integer> getAttributes(int fromIndex, Map<String, Object> context, LNode... tokens) {

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
