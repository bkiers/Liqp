package liqp.tags;

import liqp.nodes.LNode;

import java.util.HashMap;
import java.util.Map;

class For extends Tag {

    private static final String OFFSET = "offset";
    private static final String LIMIT = "limit";

    /*
     * For loop
     */
    @Override
    public Object render(Map<String, Object> variables, LNode... tokens) {

        boolean array = super.asBoolean(tokens[0].render(variables));

        String id = super.asString(tokens[1].render(variables));

        return array ? renderArray(id, variables, tokens) : renderRange(id, variables, tokens);
    }

    private Object renderArray(String id, Map<String, Object> variables, LNode... tokens) {

        StringBuilder builder = new StringBuilder();

        // attributes start from index 4
        Map<String, Integer> attributes = getAttributes(4, variables, tokens);

        int offset = attributes.get(OFFSET);
        int limit = attributes.get(LIMIT);

        Object[] array = super.asArray(tokens[2].render(variables));

        LNode block = tokens[3];

        for(int i = offset, n = 0; n < limit && i < array.length; i++, n++) {

            variables.put(id, array[i]);

            builder.append(super.asString(block.render(variables)));
        }

        return builder.toString();
    }

    private Object renderRange(String id, Map<String, Object> variables, LNode... tokens) {

        StringBuilder builder = new StringBuilder();

        // attributes start from index 5
        Map<String, Integer> attributes = getAttributes(5, variables, tokens);

        int offset = attributes.get(OFFSET);
        int limit = attributes.get(LIMIT);

        LNode block = tokens[4];

        try {
            int from = super.asNumber(tokens[2].render(variables)).intValue();
            int to = super.asNumber(tokens[3].render(variables)).intValue();

            for(int i = from + offset, n = 0; i <= to && n < limit; i++, n++) {

                variables.put(id, i);

                builder.append(super.asString(block.render(variables)));
            }
        }
        catch (Exception e) {
            /* just ignore incorrect expressions */
        }

        return builder.toString();
    }

    private Map<String, Integer> getAttributes(int fromIndex, Map<String, Object> variables, LNode... tokens) {

        Map<String, Integer> attributes = new HashMap<String, Integer>();

        attributes.put(OFFSET, 0);
        attributes.put(LIMIT, Integer.MAX_VALUE);

        for(int i = fromIndex; i < tokens.length; i++) {

            Object[] attribute = super.asArray(tokens[i].render(variables));

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
