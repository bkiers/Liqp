package liqp.tags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import liqp.LValue;
import liqp.TemplateContext;
import liqp.exceptions.ExceededMaxIterationsException;
import liqp.nodes.AtomNode;
import liqp.nodes.BlockNode;
import liqp.nodes.LNode;
import liqp.parser.Inspectable;
import liqp.parser.LiquidSupport;

/**
 * Documentation:
 * https://shopify.dev/docs/themes/liquid/reference/tags/iteration-tags
 * https://shopify.github.io/liquid/tags/iteration/
 * https://shopify.dev/docs/themes/liquid/reference/objects/for-loops
 *
 */
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
    static final String FORLOOP = "forloop";
    static final String LENGTH = "length";
    static final String INDEX = "index";
    static final String INDEX0 = "index0";
    static final String RINDEX = "rindex";
    static final String RINDEX0 = "rindex0";
    static final String FIRST = "first";
    static final String LAST = "last";
    static final String NAME = "name";
    static final String PARENTLOOP = "parentloop";

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
        String tagName = id + "-" + nodes[5].render(context);
        boolean reversed = super.asBoolean(nodes[6].render(context));

        // Each for tag has its own context that keeps track of its own variables (scope)
        TemplateContext nestedContext = new TemplateContext(context);

        Object rendered = array ? renderArray(id, nestedContext, tagName, reversed, nodes) : renderRange(id, nestedContext, tagName, reversed, nodes);

        return rendered;
    }

    private Object renderArray(String id, TemplateContext context, String tagName, boolean reversed, LNode... tokens) {

        StringBuilder builder = new StringBuilder();

        Object data = tokens[2].render(context);
        if (AtomNode.isEmpty(data) || "".equals(data)) {
            data = new ArrayList<>();
        }

        // attributes start from index 7
        Map<String, Integer> attributes = getAttributes(7, context, tagName, tokens);

        int from = attributes.get(OFFSET);
        int limit = attributes.get(LIMIT);

        if (data instanceof Inspectable) {
            LiquidSupport evaluated = context.renderSettings.evaluate(context, (Inspectable) data);
            data = evaluated.toLiquid();
        }
        if (data instanceof Map) {
            data = mapAsArray((Map) data);
        }
        Object[] array = super.asArray(data);

        LNode block = tokens[3];
        LNode blockIfEmptyOrNull = tokens[4];

        if(array == null || array.length == 0) {
            return blockIfEmptyOrNull == null ? null : blockIfEmptyOrNull.render(context);
        }


        // these conversions still works with original array without cloning
        // by just fixing offsets
        // from - from
        // to = limit ? limit.to_i + from : nil
        int to;
        if (limit > -1) {
            to = Math.min(from + limit, array.length);
        } else {
            to = array.length;
        }
        from = Math.min(from, array.length);
        int length = to - from;

        List<Object> arrayList = Arrays.asList(array).subList(from, to);
        if (reversed) {
            ArrayList<Object> listCopy = new ArrayList<>(arrayList);
            Collections.reverse(listCopy);
            arrayList = listCopy;
        }

        // now the current offset and limit is known, so its safe to set "continue" lexem
        // in case of fail it will fail
        // offsets[@name] = from + segment.length
        Map<String, Integer> registry = context.getRegistry(TemplateContext.REGISTRY_FOR);
        registry.put(tagName, from + length);

        ForLoopDrop forLoopDrop = createLoopDropInStack(context, tagName, length);
        try {
            for (Object o : arrayList) {
                context.incrementIterations();
                context.put(id, o);
                boolean isBreak = renderForLoopBody(context, builder, ((BlockNode) block).getChildren());
                forLoopDrop.increment();
                if (isBreak) {
                    break;
                }
            }
        } finally {
            popLoopDropFromStack(context);
        }

        return builder.toString();
    }

    private ForLoopDrop createLoopDropInStack(TemplateContext context, String tagName, int length) {
        Stack<ForLoopDrop> stack = getParentForloopDropStack(context);
        ForLoopDrop parent = null;
        if (!stack.empty()) {
            parent = stack.peek();
        }
        ForLoopDrop forLoopDrop =  new ForLoopDrop(tagName, length, parent);
        stack.push(forLoopDrop);
        context.put(FORLOOP, forLoopDrop);
        return forLoopDrop;
    }

    public void popLoopDropFromStack(TemplateContext context) {
        Stack<ForLoopDrop> stack = getParentForloopDropStack(context);
        if (!stack.isEmpty()) {
            stack.pop();
        }
    }


    private boolean renderForLoopBody(TemplateContext context, StringBuilder builder, List<LNode> children) {
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
            } else {
                builder.append(super.asString(value));
            }
        }
        return isBreak;
    }

    private Object renderRange(String id, TemplateContext context, String tagName, boolean reversed, LNode... tokens) {

        StringBuilder builder = new StringBuilder();

        // attributes start from index 7
        Map<String, Integer> attributes = getAttributes(7, context, tagName, tokens);

        int offset = attributes.get(OFFSET);
        int limit = attributes.get(LIMIT);

        LNode block = tokens[4];

        int from = super.asNumber(tokens[2].render(context)).intValue();
        int to   = super.asNumber(tokens[3].render(context)).intValue();
        int effectiveTo;
        if (limit < 0) {
            effectiveTo = to;
        } else {
            // 1 is because ranges right is inclusive
            effectiveTo = Math.min(to, from + limit - 1);
        }

        int length = (to - from);

        ForLoopDrop forLoopDrop = createLoopDropInStack(context, tagName, length);
        try {

            for (int i = from + offset; i <= effectiveTo; i++) {
                int realI;
                if (reversed) {
                    realI = effectiveTo - (i - from - offset);
                } else {
                    realI = i;
                }

                context.incrementIterations();
                context.put(id, realI);
                boolean isBreak = renderForLoopBody(context, builder, ((BlockNode)block).getChildren());
                forLoopDrop.increment();
                if(isBreak) {
                    // break from outer loop
                    break;
                }
            }
        } finally {
            popLoopDropFromStack(context);
        }


        return builder.toString();
    }

    private Stack<ForLoopDrop> getParentForloopDropStack(TemplateContext context) {
        Map<String, Stack<ForLoopDrop>> registry = context.getRegistry(TemplateContext.REGISTRY_FOR_STACK);
        Stack<ForLoopDrop> stack = registry.get(TemplateContext.REGISTRY_FOR_STACK);
        if (stack == null) {
            stack = new Stack<>();
            registry.put(TemplateContext.REGISTRY_FOR_STACK, stack);
        }
        return stack;
    }

    private Map<String, Integer> getAttributes(int fromIndex, TemplateContext context, String tagName, LNode... tokens) {

        Map<String, Integer> attributes = new HashMap<String, Integer>();

        attributes.put(OFFSET, 0);
        attributes.put(LIMIT, -1);

        for (int i = fromIndex; i < tokens.length; i++) {

            LNode token = tokens[i];
            Object[] attribute = super.asArray(token.render(context));
            // offset:continue
            if (OFFSET.equals(super.asString(attribute[0])) && attribute[1] == LValue.CONTINUE) {
                //      offsets = context.registers[:for] ||= {}
                //      from = if @from == :continue
                //        offsets[@name].to_i
                //      else
                //        context.evaluate(@from).to_i
                //      end
                Map<String, Integer> offsets = context.getRegistry(TemplateContext.REGISTRY_FOR);
                attributes.put(OFFSET, offsets.get(tagName));
            } else {
                try {
                    attributes.put(super.asString(attribute[0]), super.asNumber(attribute[1]).intValue());
                }
                catch (Exception e) {
                    /* just ignore incorrect attributes */
                }
            }
        }

        return attributes;
    }

    public static class ForLoopDrop implements LiquidSupport {

        private final Map<String, Object> map = new HashMap<>();

        private final ForLoopDrop parentloop;

        private int index;

        private int length;

        public ForLoopDrop(String forName, int length, ForLoopDrop parent) {
            map.put(NAME, forName);
            this.length = length;
            this.index = 0;
            this.parentloop = parent;
        }

        @Override
        public Map<String, Object> toLiquid() {
            map.put(LENGTH, length);
            map.put(INDEX, index + 1);
            map.put(INDEX0, index);
            map.put(RINDEX, length - index);
            map.put(RINDEX0, length - index - 1);
            boolean first = (index == 0);
            boolean last = (index == (length-1));
            map.put(FIRST, first);
            map.put(LAST, last);
            if (parentloop != null) {
                map.put(PARENTLOOP, parentloop);
            }
            return map;
        }

        public void increment() {
            index++;
        }
    }
}
