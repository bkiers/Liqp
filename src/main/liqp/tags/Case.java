package liqp.tags;

import liqp.LValue;
import liqp.nodes.LNode;

import java.util.Map;

class Case extends Tag {

    /*
     * Block tag, its the standard case...when block
     */
    @Override
    public Object render(Map<String, Object> variables, LNode... tokens) {

        Object condition = tokens[0].render(variables);

        for(int i = 1; i < tokens.length - 1; i += 2) {

            Object whenExpressionValue = tokens[i].render(variables);
            LNode whenBlock = tokens[i + 1];

            if(LValue.areEqual(condition, whenExpressionValue)) {
                return whenBlock.render(variables);
            }
        }

        return "";
    }
}
