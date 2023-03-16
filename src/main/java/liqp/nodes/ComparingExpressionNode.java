package liqp.nodes;

import liqp.LValue;
import liqp.TemplateContext;

public abstract class ComparingExpressionNode extends LValue implements LNode {
    protected final LNode lhs;
    protected final LNode rhs;

    public ComparingExpressionNode(LNode lhs, LNode rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public Object render(TemplateContext context) {

        Object a = lhs.render(context);
        Object b = rhs.render(context);
        if (isTemporal(a)) {
            a = asTemporal(a, context);
        }
        if (isTemporal(b)) {
            b = asTemporal(b, context);
        }

        if (canBeDouble(b) || canBeInteger(b)) {
            b = asNumber(b);
        }
        if (canBeDouble(a) || canBeInteger(a)) {
            a = asNumber(a);
        }

        return doCompare(a, b);
    }

    abstract Object doCompare(Object a, Object b);
}
