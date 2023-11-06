package liqp.nodes;

import liqp.TemplateContext;
import liqp.Insertion;

import java.util.List;

public class InsertionNode implements LNode {

    private final Insertion insertion;
    private final LNode[] tokens;

    public InsertionNode(Insertion insertion, List<LNode> tokens) {
        this(insertion.name, insertion, tokens.toArray(new LNode[0]));
    }

    public InsertionNode(Insertion insertion, LNode... tokens) {
        this(insertion.name, insertion, tokens);
    }

    public InsertionNode(String tagName, Insertion insertion, LNode... tokens) {
        if (tagName == null) {
            throw new IllegalArgumentException("tagName == null");
        }
        if (insertion == null) {
            throw new IllegalArgumentException("no tag available named: " + tagName);
        }
        this.insertion = insertion;
        this.tokens = tokens;
    }

    @Override
    public Object render(TemplateContext context) {
        return insertion.render(context, tokens);
    }
}
