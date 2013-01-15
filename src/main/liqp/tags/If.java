package liqp.tags;

import liqp.nodes.LNode;

import java.util.Map;

class If extends Tag {

    @Override
    public Object render(Map<String, Object> variables, LNode... tokens) {

        for(int i = 0; i < tokens.length - 1; i += 2) {

            Object exprNodeValue = tokens[i].render(variables);
            LNode blockNode = tokens[i + 1];

            if(super.asBoolean(exprNodeValue)) {
                return blockNode.render(variables);
            }
        }

        return "";
    }
}
