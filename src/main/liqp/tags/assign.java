package liqp.tags;

import liqp.nodes.LNode;

import java.util.Map;

public class assign extends Tag {

    @Override
    public Object render(Map<String, Object> variables, LNode... tokens) {

        String id = String.valueOf(tokens[0].render(variables));
        LNode expression = tokens[1];

        Object value = expression.render(variables);

        variables.put(id, value);

        return "";
    }
}
