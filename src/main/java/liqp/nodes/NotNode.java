package liqp.nodes;

import liqp.LValue;
import liqp.TemplateContext;

class NotNode extends LValue implements LNode {

    private LNode rhs;

    public NotNode(LNode rhs) {
        this.rhs = rhs;
    }

    @Override
    public Object render(TemplateContext context) {

        Object a = rhs.render(context);

        return !(super.asBoolean(a));

    }
}
