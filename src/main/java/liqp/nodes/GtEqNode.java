package liqp.nodes;

import liqp.LValue;
import liqp.TemplateContext;

public class GtEqNode extends LValue implements LNode {

    private final LNode lhs;
    private final LNode rhs;

    public GtEqNode(LNode lhs, LNode rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public Object render(TemplateContext context) {

        Object a = lhs.render(context);
        Object b = rhs.render(context);
        if (isTemporal(a)) {
            a = asTemporal(a);
        }
        if (isTemporal(b)) {
            b = asTemporal(b);
        }

        if (a instanceof Comparable && a.getClass().isInstance(b)) {
            return ((Comparable) a).compareTo(b) >= 0;
        } else if (b instanceof Comparable && b.getClass().isInstance(a)) {
            return ((Comparable) b).compareTo(a) < 0;
        }

        // different number class so use this convertion
        return (a instanceof Number) && (b instanceof Number) &&
                super.asNumber(a).doubleValue() >= super.asNumber(b).doubleValue();
    }
}
