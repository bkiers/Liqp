package liqp.nodes;

import liqp.LValue;
import liqp.TemplateContext;

public class AndNode extends LValue implements LNode {

    private LNode lhs;
    private LNode rhs;

    public AndNode(LNode lhs, LNode rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public Object render(TemplateContext context) {

        return super.asBoolean(lhs.render(context)) && super.asBoolean(rhs.render(context));

    }
}
