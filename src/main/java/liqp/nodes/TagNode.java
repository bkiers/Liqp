package liqp.nodes;

import liqp.tags.Tag;

import java.util.Map;

class TagNode implements LNode {

    private Tag tag;
    private LNode[] tokens;

    public TagNode(String tagName, Tag tag, LNode... tokens) {
        if (tag == null) {
            throw new IllegalArgumentException("no tag available named: " + tagName);
        }
        this.tag = tag;
        this.tokens = tokens;
    }

    @Override
    public Object render(Map<String, Object> context) {

        return tag.render(context, tokens);
    }
}
