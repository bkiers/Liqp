package liqp.nodes;

import liqp.tags.Tag;

import java.util.Map;

public class TagNode implements LNode {

    private Tag tag;
    private LNode[] tokens;

    public TagNode(String name, LNode... tokens) {
        this.tag = Tag.getTag(name);
        this.tokens = tokens;
    }

    @Override
    public Object render(Map<String, Object> variables) {

        return tag.render(variables, tokens);
    }
}
