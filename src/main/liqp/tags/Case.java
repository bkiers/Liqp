package liqp.tags;

import liqp.LValue;
import liqp.nodes.LNode;

import java.util.Map;

class Case extends Tag {

    /*
     * Block tag, its the standard case...when block
     */
    @Override
    public Object render(Map<String, Object> context, LNode... nodes) {

        Object condition = nodes[0].render(context);

        for(int i = 1; i < nodes.length - 1; i += 2) {

            Object whenExpressionValue = nodes[i].render(context);
            LNode whenBlock = nodes[i + 1];

            if(LValue.areEqual(condition, whenExpressionValue)) {
                return whenBlock.render(context);
            }
        }

        return "";
    }
}
