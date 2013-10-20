package liqp.nodes;

import liqp.Context;
import liqp.LValue;

class GtNode extends LValue implements LNode {

    private LNode lhs;
    private LNode rhs;

    public GtNode(LNode lhs, LNode rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public Object render(Context context) {

        Object a = lhs.render(context);
        Object b = rhs.render(context);

        return (a instanceof Number) && (b instanceof Number) &&
                super.asNumber(a).doubleValue() > super.asNumber(b).doubleValue();
    }
}
