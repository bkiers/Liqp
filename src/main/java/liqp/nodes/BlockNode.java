package liqp.nodes;

import java.util.ArrayList;
import java.util.List;

import liqp.TemplateContext;
import liqp.tags.Tag;

import static liqp.LValue.BREAK;
import static liqp.LValue.CONTINUE;

public class BlockNode implements LNode {

    private List<LNode> children;
    private final boolean isRootBlock;

    public BlockNode() {
        this(false);
    }

    public BlockNode(boolean isRootBlock) {
        this.children = new ArrayList<LNode>();
        this.isRootBlock = isRootBlock;
    }

    public void add(LNode node) {
        children.add(node);
    }

    public List<LNode> getChildren() {
        return new ArrayList<LNode>(children);
    }

    @Override
    public Object render(TemplateContext context) {

        StringBuilder builder = new StringBuilder();

        for (LNode node : children) {

            Object value = node.render(context);

            if(value == null) {
                continue;
            }

            if(value == BREAK || value == CONTINUE) {
                return value;
            }
            else if (value instanceof List) {

                List list = (List) value;

                for (Object obj : list) {
                    builder.append(String.valueOf(obj));
                }
            }
            else if (value.getClass().isArray()) {

                Object[] array = (Object[]) value;

                for (Object obj : array) {
                    builder.append(String.valueOf(obj));
                }
            }
            else {
                builder.append(String.valueOf(value));
            }

            if (builder.length() > context.protectionSettings.maxSizeRenderedString) {
                throw new RuntimeException("rendered string exceeds " + context.protectionSettings.maxSizeRenderedString);
            }
        }

        return builder.toString();
    }
}
