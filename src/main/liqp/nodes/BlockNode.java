package liqp.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BlockNode implements LNode {

    private List<LNode> children;

    public BlockNode() {
        children = new ArrayList<LNode>();
    }

    public void add(LNode node) {
        children.add(node);
    }

    @Override
    public Object render(Map<String, Object> variables) {

        StringBuilder builder = new StringBuilder();

        for(LNode node : children) {

            Object value = node.render(variables);

            if(value != null) {

                if(value.getClass().isArray()) {

                    Object[] array = (Object[])value;

                    for(Object obj : array) {
                        builder.append(String.valueOf(obj));
                    }
                }
                else {
                    builder.append(String.valueOf(value));
                }
            }
        }

        return builder.toString();
    }
}
