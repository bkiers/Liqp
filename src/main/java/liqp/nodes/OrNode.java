package liqp.nodes;

import liqp.LValue;
import liqp.TemplateContext;

class OrNode extends LValue implements LNode {

    private LNode lhs;
    private LNode rhs;

    public OrNode(LNode lhs, LNode rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public Object render(TemplateContext context) {

        return super.asBoolean(lhs.render(context)) || super.asBoolean(rhs.render(context));

    }
}
