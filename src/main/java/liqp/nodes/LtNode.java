package liqp.nodes;

import liqp.LValue;
import liqp.TemplateContext;

public class LtNode extends LValue implements LNode {

    private LNode lhs;
    private LNode rhs;

    public LtNode(LNode lhs, LNode rhs) {
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
            return ((Comparable) a).compareTo(b) < 0;
        } else if (b instanceof Comparable && b.getClass().isInstance(a)) {
            return ((Comparable) b).compareTo(a) >= 0;
        }

        return (a instanceof Number) && (b instanceof Number) &&
                super.asNumber(a).doubleValue() < super.asNumber(b).doubleValue();
    }
}
