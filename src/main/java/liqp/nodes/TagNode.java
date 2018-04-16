package liqp.nodes;

import liqp.TemplateContext;
import liqp.parser.Flavor;
import liqp.tags.Include;
import liqp.tags.Tag;

import java.io.File;
import java.util.List;
import java.util.Map;

public class TagNode implements LNode {

    private Tag tag;
    private LNode[] tokens;

    public TagNode(Tag tag, List<LNode> tokens) {
        this(tag.name, tag, tokens.toArray(new LNode[tokens.size()]));
    }

    public TagNode(Tag tag, LNode... tokens) {
        this(tag.name, tag, tokens);
    }

    public TagNode(String tagName, Tag tag, LNode... tokens) {
        if (tagName == null) {
            throw new IllegalArgumentException("tagName == null");
        }
        if (tag == null) {
            throw new IllegalArgumentException("no tag available named: " + tagName);
        }
        this.tag = tag;
        this.tokens = tokens;
    }

    @Override
    public Object render(TemplateContext context) {
        return tag.render(context, tokens);
    }
}
