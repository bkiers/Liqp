package liqp.tags;

import liqp.nodes.LNode;

import java.util.Map;

class Capture extends Tag {

    /*
     * Block tag that captures text into a variable
     */
    @Override
    public Object render(Map<String, Object> variables, LNode... tokens) {

        String id = super.asString(tokens[0].render(variables));

        LNode block = tokens[1];

        return variables.put(id, block.render(variables));
    }
}
