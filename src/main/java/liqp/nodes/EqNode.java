package liqp.nodes;

import liqp.Context;
import liqp.LValue;

class EqNode implements LNode {

    private LNode lhs;
    private LNode rhs;

    public EqNode(LNode lhs, LNode rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public Object render(Context context) {

        Object a = lhs.render(context);
        Object b = rhs.render(context);

        return LValue.areEqual(a, b);

    }
}
