package liqp.nodes;

import liqp.Context;
import liqp.tags.Tag;

class TagNode implements LNode {

    private Tag tag;
    private LNode[] tokens;

    public TagNode(String name, LNode... tokens) {
        this.tag = Tag.getTag(name);
        this.tokens = tokens;
    }

    @Override
    public Object render(Context context) {

        return tag.render(context, tokens);
    }
}
